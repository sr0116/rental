package com.imchobo.lease.controller;

import com.imchobo.lease.domain.dto.PaymentCompleteDTO;
import com.imchobo.lease.domain.dto.PaymentDTO;
import com.imchobo.lease.domain.dto.PaymentPrepareDTO;
import com.imchobo.lease.domain.dto.RefundRequestDTO;
import com.imchobo.lease.service.PaymentService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * PaymentController
 * - 결제 관련 REST API 엔드포인트 제공
 * - 프론트엔드(Next.js)에서 호출하는 진입점
 */
@RestController
// @RestController: @Controller + @ResponseBody
// → API 응답을 JSON 형태로 반환
@RequestMapping("/api/payments")
// @RequestMapping: 모든 엔드포인트 공통 prefix (/api/payments)
@RequiredArgsConstructor
// @RequiredArgsConstructor: final 필드 주입용 생성자 자동 생성
@Slf4j
// @Slf4j: 로깅(log) 기능 제공 (log.info 등)
public class PaymentController {

  /**
   * 결제 서비스 (비즈니스 로직 담당)
   */
  private final PaymentService paymentService;

  /**
   * 결제 준비 API
   * - 프론트에서 결제 버튼 누르면 호출
   * - DB에 상태 PENDING으로 저장
   */
  @PostMapping("/prepare")
  public PaymentDTO prepare(@RequestBody PaymentPrepareDTO dto) {
    log.info("결제 준비 API 호출 - orderId: {}, amount: {}", dto.getOrderId(), dto.getAmount());
    return paymentService.prepare(dto);
  }

  @PostMapping("/complete")
  public PaymentDTO complete(@RequestBody PaymentCompleteDTO dto) {
    log.info("결제 완료 API 호출 - merchantUid: {}", dto.getMerchantUid());
    return paymentService.complete(dto);
  }

  /**
   * 환불 API
   * - 구독 만료 시 전체 환불
   * - 조기 해지 시 위약금 차감 후 부분 환불
   */


  @PostMapping("/refund")
  public ResponseEntity<?> refund(@RequestBody RefundRequestDTO dto) {
    log.info("환불 API 호출 - paymentId: {}, amount: {}", dto.getPaymentId(), dto.getAmount());

    try {
      PaymentDTO response = paymentService.refund(dto);

      // 성공 응답
      Map<String, Object> result = new HashMap<>();
      result.put("success", true);
      result.put("data", response);
      return ResponseEntity.ok(result);

    } catch (IllegalArgumentException e) {
      log.error("환불 실패 - 잘못된 요청: {}", e.getMessage());

      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);

    } catch (Exception e) {
      log.error("환불 처리 중 서버 오류", e);

      Map<String, Object> error = new HashMap<>();
      error.put("success", false);
      error.put("error", "환불 처리 중 오류가 발생했습니다");
      error.put("details", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
  }
}
