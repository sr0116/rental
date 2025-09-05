package com.imchobo.lease.service.serviceimpl;


import com.imchobo.lease.PortOneClient;
import com.imchobo.lease.config.PortOneClientConfig;
import com.imchobo.lease.domain.dto.PaymentCompleteDTO;
import com.imchobo.lease.domain.dto.PaymentDTO;
import com.imchobo.lease.domain.dto.PaymentPrepareDTO;
import com.imchobo.lease.domain.dto.RefundRequestDTO;
import com.imchobo.lease.domain.entity.Payment;
import com.imchobo.lease.domain.entity.Refund;
import com.imchobo.lease.mapper.PaymentMapper;
import com.imchobo.lease.mapper.RefundMapper;
import com.imchobo.lease.repository.PaymentRepository;
import com.imchobo.lease.repository.RefundRepository;
import com.imchobo.lease.repository.PaymentHistoryRepository;
import com.imchobo.lease.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * PaymentServiceImpl
 * - PaymentService 인터페이스 구현체
 * - PortOne API 연동 및 DB 상태 전이 처리 담당
 */
@Service
// @Service: 스프링이 이 클래스를 서비스 레이어 Bean 으로 등록
@RequiredArgsConstructor
// @RequiredArgsConstructor: final 필드를 매개변수로 받는 생성자 자동 생성 → 의존성 주입
@Slf4j
// @Slf4j: log.info(), log.error() 같은 로깅 메서드 사용 가능
public class PaymentServiceImpl implements PaymentService {

  /**
   * 결제 테이블 접근
   */
  private final PaymentRepository paymentRepository;

  /**
   * 환불 테이블 접근
   */
  private final RefundRepository refundRepository;

  /**
   * 결제 이력 테이블 접근
   */
  private final PaymentHistoryRepository paymentHistoryRepository;

  /**
   * DTO ↔ Entity 매핑
   */
  private final PaymentMapper paymentMapper;
  private final RefundMapper refundMapper;
  private final PortOneClient portOneClient;

  /**
   * 결제 준비
   * - PortOne 사전 등록 (현재는 가짜 merchantUid 생성으로 대체)
   * - DB에 상태 PENDING 으로 저장
   */

  @Override
  @Transactional
  public PaymentDTO prepare(PaymentPrepareDTO dto) {
    log.info("결제 준비 시작 - orderId: {}, amount: {}", dto.getOrderId(), dto.getAmount());

    Payment entity = paymentMapper.toEntity(dto);

    // merchantUid 생성 → 프론트로 넘겨야 함
    String merchantUid = "MCHT-" + System.currentTimeMillis();
    entity.setMerchantUid(merchantUid);
    entity.setPaystatus("PENDING");

    Payment saved = paymentRepository.save(entity);
    return paymentMapper.toDto(saved);
  }

  @Override
  @Transactional
  public PaymentDTO complete(PaymentCompleteDTO dto) {
    log.info("결제 완료 검증 시작 - merchantUid: {}", dto.getMerchantUid());

    Payment entity = paymentRepository.findByMerchantUid(dto.getMerchantUid())
            .orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없음: " + dto.getMerchantUid()));

    // PortOne 결제 검증 후 impUid 저장
    entity.setImpUid(dto.getImpUid());  // ★ 이 라인 꼭 있어야 함
    entity.setPaystatus("PAID");

    Payment saved = paymentRepository.save(entity);
    return paymentMapper.toDto(saved);
  }


  @Override
  @Transactional
  public PaymentDTO refund(RefundRequestDTO dto) {
    log.info("환불 요청 시작 - paymentId: {}, amount: {}", dto.getPaymentId(), dto.getAmount());

    Payment entity = paymentRepository.findById(dto.getPaymentId())
            .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없음: " + dto.getPaymentId()));

    String impUid = entity.getImpUid();
    if (impUid == null || impUid.isBlank()) {
      throw new IllegalStateException("해당 결제 건에 impUid가 없습니다.");
    }

    // 1. PortOne API 호출
    Map cancelRes = portOneClient.cancelPayment(
            impUid,
            (dto.getAmount() != null ? dto.getAmount().intValue() : null),
            dto.getReason()
    );
    log.info("PortOne 환불 응답: {}", cancelRes);

    // 2. PortOne 응답 검증
    Integer code = (Integer) cancelRes.get("code");
    if (code == null || code != 0) {
      throw new RuntimeException("PortOne 환불 실패: " + cancelRes);
    }

    Map response = (Map) cancelRes.get("response");
    if (response == null) {
      throw new RuntimeException("PortOne 환불 실패 (response 없음): " + cancelRes);
    }

    // 3. 상태 업데이트
    if (dto.getAmount() != null && dto.getAmount().compareTo(entity.getAmount()) < 0) {
      entity.setPaystatus("PARTIAL_REFUNDED");
    } else {
      entity.setPaystatus("REFUNDED");
    }
    entity.setVoiddate(LocalDateTime.now());

    // 4. 환불 기록 저장
    Refund refund = refundMapper.toEntity(dto);
    refund.setPaymentId(entity.getPaymentId());
    refundRepository.save(refund);

    // 5. 결제 저장 후 반환
    Payment saved = paymentRepository.save(entity);
    log.info("환불 완료 - paymentId: {}, status: {}", saved.getPaymentId(), saved.getPaystatus());

    return paymentMapper.toDto(saved);
  }


}
