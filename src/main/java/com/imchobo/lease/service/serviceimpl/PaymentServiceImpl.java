package com.imchobo.lease.service.serviceimpl;


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

  /** 결제 테이블 접근 */
  private final PaymentRepository paymentRepository;

  /** 환불 테이블 접근 */
  private final RefundRepository refundRepository;

  /** 결제 이력 테이블 접근 */
  private final PaymentHistoryRepository paymentHistoryRepository;

  /** DTO ↔ Entity 매핑 */
  private final PaymentMapper paymentMapper;
  private final RefundMapper refundMapper;

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

    entity.setImpUid(dto.getImpUid());
    entity.setPaystatus("PAID");

    Payment saved = paymentRepository.save(entity);
    return paymentMapper.toDto(saved);
  }


  /**
   * 환불 처리
   * - 위약금이 있으면 부분 환불, 없으면 전체 환불
   * - PortOne 환불 API 호출 → 여기선 단순 로직으로 대체
   */
  @Override
  @Transactional
  public PaymentDTO refund(RefundRequestDTO dto) {
    log.info("환불 요청 시작 - paymentId: {}, amount: {}", dto.getPaymentId(), dto.getAmount());

    // 1. 결제 엔티티 조회
    Payment entity = paymentRepository.findById(dto.getPaymentId())
            .orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없음: " + dto.getPaymentId()));

    // 2. 환불 처리 (실제 PortOne API 호출 필요)
    entity.setPaystatus("REFUNDED");
    entity.setVoiddate(java.time.LocalDateTime.now());

    // 3. Refund 엔티티 생성 및 저장
    Refund refund = refundMapper.toEntity(dto);
    refund.setPaymentId(entity.getPaymentId());
    refundRepository.save(refund);

    // 4. 결제 상태 저장
    Payment saved = paymentRepository.save(entity);

    return paymentMapper.toDto(saved);
  }
}