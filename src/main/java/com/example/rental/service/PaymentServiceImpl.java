package com.example.rental.service;

import com.example.rental.client.PaymentGatewayClient;
import com.example.rental.client.PaymentGatewayClientFactory;
import com.example.rental.domain.*;
import com.example.rental.domain.dto.RentalPaymentDTO;
import com.example.rental.mapper.RentalPaymentMapper;
import com.example.rental.repository.RentalPaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.rental.domain.RentalPayment.PayStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

  private final RentalPaymentRepository rentalPaymentRepository;
  private final RentalPaymentMapper rentalPaymentMapper;
  private final PaymentGatewayClientFactory clientFactory;

  /** 결제 준비 (사전등록) */
  @Override
  public ApiResponse<Void> prepare(PaymentPrepareRequest req) {
    String merchantUid = req.getMerchantUid();
    Integer amount = req.getAmount();
    RentalPayment.PlanType planType = StringUtils.hasText(req.getPlanType())
            ? RentalPayment.PlanType.valueOf(req.getPlanType())
            : RentalPayment.PlanType.ONE_TIME;

    // DB 저장
    RentalPayment p = rentalPaymentRepository.findByMerchantUid(merchantUid)
            .orElseGet(RentalPayment::new);
    if (p.getId() == null) p.setMerchantUid(merchantUid);

    p.setAmount(amount);
    p.setPlanType(planType);
    p.setUserEmail(req.getUserEmail());
    p.setItemId(req.getItemId());
    p.setOpenId(req.getOpenId());
    p.setCustomerUid(req.getCustomerUid());
    p.setStatus(PENDING);
    rentalPaymentRepository.save(p);

    // PortOne 사전등록 API 호출
    boolean recurring = planType == RentalPayment.PlanType.RECURRING;
    PaymentGatewayClient pgClient = clientFactory.getClient(recurring);

    boolean ok = pgClient.prepare(merchantUid, amount);
    return ok ? ApiResponse.ok(null) : ApiResponse.fail("prepare failed");
  }

  /** 결제 완료 검증 */
  @Override
  public ApiResponse<Object> complete(PaymentCompleteRequest req) {
    RentalPayment order = rentalPaymentRepository.findByMerchantUid(req.getMerchantUid())
            .orElseThrow(() -> new RuntimeException("order not found"));

    PaymentGatewayClient pgClient = clientFactory.getClient(
            order.getPlanType() == RentalPayment.PlanType.RECURRING
    );

    JsonNode pay = pgClient.verify(req.getImpUid());
    if (pay == null) {
      return ApiResponse.fail("verify failed");
    }

    String merchantUid = pay.get("merchant_uid").asText();
    String status = pay.get("status").asText();
    int paidAmount = pay.get("amount").asInt();
    String pgTid = pay.hasNonNull("pg_tid") ? pay.get("pg_tid").asText() : null;

    boolean ok = merchantUid.equals(req.getMerchantUid())
            && "paid".equals(status)
            && paidAmount == order.getAmount();

    if (!ok) {
      order.setStatus(FAILED);
      order.setFailReason("mismatch or not paid(" + status + ")");
      order.setImpUid(req.getImpUid());
      order.setPgTid(pgTid);
      rentalPaymentRepository.save(order);
      return ApiResponse.fail("mismatch or not paid");
    }

    order.setStatus(PAID);
    order.setPaidAmount(paidAmount);
    order.setPaidAt(LocalDateTime.now());
    order.setImpUid(req.getImpUid());
    order.setPgTid(pgTid);
    rentalPaymentRepository.save(order);

    return ApiResponse.ok(rentalPaymentMapper.toDto(order));
  }

  /** 단건 환불 */
  @Override
  public ApiResponse<Object> refund(RefundRequest req) {
    RentalPayment p = rentalPaymentRepository.findByMerchantUid(req.getMerchantUid())
            .orElseThrow(() -> new RuntimeException("record not found"));

    if (!StringUtils.hasText(p.getImpUid())) {
      return ApiResponse.fail("imp_uid not found for merchantUid");
    }

    PaymentGatewayClient pgClient = clientFactory.getClient(
            p.getPlanType() == RentalPayment.PlanType.RECURRING
    );

    boolean refunded = pgClient.refund(
            p.getImpUid(),
            req.getAmount() != null ? req.getAmount() : p.getPaidAmount(),
            req.getReason()
    );
    if (!refunded) {
      return ApiResponse.fail("refund failed");
    }

    p.setStatus(REFUNDED);
    int refundAmt = req.getAmount() != null ? req.getAmount() : p.getPaidAmount();
    p.setRefundAmount(refundAmt);
    p.setRefundedAt(LocalDateTime.now());
    rentalPaymentRepository.save(p);

    return ApiResponse.ok(rentalPaymentMapper.toDto(p));
  }

  /** 단건 조회 */
  @Override
  public RentalPaymentDTO getByMerchantUid(String merchantUid) {
    return rentalPaymentRepository.findByMerchantUid(merchantUid)
            .map(rentalPaymentMapper::toDto)
            .orElseThrow(() -> new IllegalArgumentException("payment not found: " + merchantUid));
  }

  /** 최근 조회 */
  @Override
  public List<RentalPaymentDTO> getRecent(int size) {
    return rentalPaymentMapper.toDtoList(
            rentalPaymentRepository.findAll(PageRequest.of(0, Math.max(1, size))).getContent()
    );
  }

  /** 정기 결제 실행 */
  @Override
  public RentalPaymentDTO chargeRecurring(String billingKey, int amount, String userEmail, int periodDays) {
    PaymentGatewayClient pgClient = clientFactory.getClient(true);
    boolean success = pgClient.charge(billingKey, amount);
    LocalDateTime now = LocalDateTime.now();

    RentalPayment payment = RentalPayment.builder()
            .merchantUid("order_" + System.currentTimeMillis())
            .customerUid(billingKey)
            .userEmail(userEmail)
            .amount(amount)
            .planType(RentalPayment.PlanType.RECURRING)
            .payMethod(RentalPayment.PayMethod.card)
            .status(success ? PAID : FAILED)
            .paidAt(success ? now : null)
            .periodDays(periodDays)
            .serviceEndAt(now.plusDays(periodDays))
            .isRefunded(false)
            .build();

    rentalPaymentRepository.save(payment);
    return rentalPaymentMapper.toDto(payment);
  }

  /** 정기 결제 환불 (차등 환불) */
  @Override
  public int refundRecurring(Long paymentId) {
    RentalPayment payment = rentalPaymentRepository
            .findByIdAndPlanTypeAndIsRefundedFalse(paymentId, RentalPayment.PlanType.RECURRING)
            .orElseThrow(() -> new IllegalStateException("환불 불가능한 결제 내역"));

    if (!StringUtils.hasText(payment.getImpUid())) {
      throw new IllegalStateException("imp_uid not found for refund");
    }

    LocalDateTime now = LocalDateTime.now();
    long daysUsed = java.time.Duration.between(payment.getPaidAt(), now).toDays();

    int refundAmount = calculateRefund(payment.getAmount(), payment.getPeriodDays(), daysUsed);

    if (refundAmount > 0) {
      PaymentGatewayClient pgClient = clientFactory.getClient(true);
      boolean refunded = pgClient.refund(payment.getImpUid(), refundAmount, "차등 환불");
      if (refunded) {
        payment.setStatus(REFUNDED);
        payment.setRefundAmount(refundAmount);
        payment.setRefundedAt(LocalDateTime.now());
        payment.setIsRefunded(true);
        rentalPaymentRepository.save(payment);
      }
    }
    return refundAmount;
  }

  private int calculateRefund(int totalAmount, int periodDays, long daysUsed) {
    double ratio;
    if (daysUsed <= 7) {
      ratio = 0.8;
    } else if (daysUsed <= (periodDays / 2)) {
      ratio = 0.5;
    } else {
      ratio = 0.0;
    }
    return (int) (totalAmount * ratio);
  }
}
