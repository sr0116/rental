package com.imchobo.lease.repository;

import com.imchobo.lease.domain.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * PaymentHistoryRepository
 * - tbl_payment_history 테이블 접근 전용 DAO
 * - 결제 상태 변경 이력 관리
 */
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

  /**
   * 특정 결제 건에 대한 모든 상태 변경 로그 조회
   */
  List<PaymentHistory> findByPaymentId(Long paymentId);
}
