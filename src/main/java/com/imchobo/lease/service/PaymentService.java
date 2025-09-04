package com.imchobo.lease.service;

import com.imchobo.lease.domain.dto.PaymentCompleteDTO;
import com.imchobo.lease.domain.dto.PaymentDTO;
import com.imchobo.lease.domain.dto.PaymentPrepareDTO;
import com.imchobo.lease.domain.dto.RefundRequestDTO;

/**
 * PaymentService 인터페이스
 * - 결제와 관련된 주요 기능 정의
 * - Controller → Service → Repository 구조에서 "계약서" 역할
 * - 구현체 PaymentServiceImpl이 실제 로직을 수행
 */
public interface PaymentService {

  /**
   * 결제 준비
   * - PortOne 사전 등록 및 DB 저장
   * - 상태: PENDING
   */
  PaymentDTO prepare(PaymentPrepareDTO dto);

  /**
   * 결제 완료
   * - PortOne API 검증 후 DB 상태 갱신
   * - 상태: PAID
   */
  PaymentDTO complete(PaymentCompleteDTO dto);

  /**
   * 환불 처리
   * - PortOne 환불 API 호출
   * - 전체/부분 환불 가능
   * - 상태: REFUNDED
   */
  PaymentDTO refund(RefundRequestDTO dto);
}