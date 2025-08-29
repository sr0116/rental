package com.example.rental.service;

import com.example.rental.domain.RentalPayment;
import com.example.rental.domain.dto.*;
import com.example.rental.mapper.RentalPaymentMapper;
import com.example.rental.repository.RentalPaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.rental.domain.RentalPayment.PayStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

  private final RentalPaymentRepository repo;
  private final RentalPaymentMapper mapper;

  @Value("${portone.api-key}")
  private String apiKey;
  @Value("${portone.api-secret}")
  private String apiSecret;

  private final RestClient rest = RestClient.builder()
          .baseUrl("https://api.iamport.kr")
          .build();

  /** PortOne access token 발급 */
  private String token() {
    JsonNode j = rest.post()
            .uri("/users/getToken")
            .contentType(MediaType.APPLICATION_JSON)
            .body("{" +
                    "\"imp_key\":\"" + apiKey + "\"," +
                    "\"imp_secret\":\"" + apiSecret + "\"}")
            .retrieve().body(JsonNode.class);

    if (j == null || j.get("code").asInt() < 0) {
      throw new RuntimeException("token error: " + (j != null ? j.get("message") : "null"));
    }
    return j.get("response").get("access_token").asText();
  }

  /** 결제 준비 (사전등록) */
  @Override
  public ApiResponse<Void> prepare(PaymentPrepareRequest req) {
    String merchantUid = req.getMerchantUid();
    Integer amount = req.getAmount();
    String planType = StringUtils.hasText(req.getPlanType()) ? req.getPlanType() : "ONE_TIME";

    // DB에 PENDING 저장/업데이트
    Optional<RentalPayment> opt = repo.findByMerchantUid(merchantUid);
    RentalPayment p = opt.orElseGet(RentalPayment::new);
    if (p.getId() == null) p.setMerchantUid(merchantUid);
    p.setAmount(amount);
    p.setPlanType(RentalPayment.PlanType.valueOf(planType));
    p.setUserEmail(req.getUserEmail());
    p.setItemId(req.getItemId());
    p.setOpenId(req.getOpenId());
    p.setCustomerUid(req.getCustomerUid());
    p.setStatus(PENDING);
    repo.save(p);

    // PortOne 사전등록 API 호출
    String token = token();
    JsonNode j = rest.post().uri("/payments/prepare")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", token)
            .body("{" +
                    "\"merchant_uid\":\"" + merchantUid + "\"," +
                    "\"amount\":" + amount + "}")
            .retrieve().body(JsonNode.class);

    if (j == null || j.get("code").asInt() < 0) {
      return ApiResponse.fail(j != null ? j.get("message").asText() : "prepare failed");
    }
    return ApiResponse.ok(null);
  }

  /** 결제 완료 검증 */
  @Override
  public ApiResponse<Object> complete(PaymentCompleteRequest req) {
    String token = token();
    JsonNode j = rest.get().uri("/payments/" + req.getImpUid())
            .header("Authorization", token)
            .retrieve().body(JsonNode.class);

    if (j == null || j.get("code").asInt() < 0) {
      return ApiResponse.fail(j != null ? j.get("message").asText() : "verify failed");
    }
    JsonNode pay = j.get("response");

    String merchantUid = pay.get("merchant_uid").asText();
    String status = pay.get("status").asText();
    int paidAmount = pay.get("amount").asInt();
    String pgTid = pay.hasNonNull("pg_tid") ? pay.get("pg_tid").asText() : null;

    RentalPayment p = repo.findByMerchantUid(merchantUid)
            .orElseThrow(() -> new RuntimeException("order not found"));

    boolean ok = merchantUid.equals(req.getMerchantUid())
            && "paid".equals(status)
            && paidAmount == p.getAmount();

    if (!ok) {
      p.setStatus(FAILED);
      p.setFailReason("mismatch or not paid(" + status + ")");
      p.setImpUid(req.getImpUid());
      p.setPgTid(pgTid);
      repo.save(p);
      return ApiResponse.fail("mismatch or not paid");
    }

    // 결제 성공 처리
    p.setStatus(PAID);
    p.setPaidAmount(paidAmount);
    p.setPaidAt(LocalDateTime.now());
    p.setImpUid(req.getImpUid());
    p.setPgTid(pgTid);
    repo.save(p);

    return ApiResponse.ok(mapper.toDto(p));
  }

  /** 환불 */
  @Override
  public ApiResponse<Object> refund(RefundRequest req) {
    RentalPayment p = repo.findByMerchantUid(req.getMerchantUid())
            .orElseThrow(() -> new RuntimeException("record not found"));

    if (!StringUtils.hasText(p.getImpUid())) {
      return ApiResponse.fail("imp_uid not found for merchantUid");
    }

    String token = token();
    String reason = StringUtils.hasText(req.getReason()) ? req.getReason() : "refund";
    String body = req.getAmount() != null && req.getAmount() > 0
            ? "{\"imp_uid\":\"" + p.getImpUid() + "\",\"reason\":\"" + reason + "\",\"amount\":" + req.getAmount() + "}"
            : "{\"imp_uid\":\"" + p.getImpUid() + "\",\"reason\":\"" + reason + "\"}";

    JsonNode j = rest.post().uri("/payments/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", token)
            .body(body)
            .retrieve().body(JsonNode.class);

    if (j == null || j.get("code").asInt() < 0) {
      return ApiResponse.fail(j != null ? j.get("message").asText() : "refund failed");
    }
    JsonNode res = j.get("response");

    // DB 갱신
    p.setStatus(REFUNDED);
    int refundAmt = res.hasNonNull("cancel_amount")
            ? res.get("cancel_amount").asInt()
            : (req.getAmount() != null ? req.getAmount() : p.getPaidAmount());

    p.setRefundAmount(refundAmt);
    p.setRefundedAt(LocalDateTime.now());
    repo.save(p);

    return ApiResponse.ok(mapper.toDto(p));
  }

  /** 단건 조회 */
  @Override
  public RentalPaymentDTO getByMerchantUid(String merchantUid) {
    return repo.findByMerchantUid(merchantUid)
            .map(mapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("payment not found: " + merchantUid));
  }

  /** 최근 조회 */
  @Override
  public List<RentalPaymentDTO> getRecent(int size) {
    return mapper.toDtoList(
            repo.findAll(PageRequest.of(0, Math.max(1, size))).getContent()
    );
  }
}
