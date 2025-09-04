package com.imchobo.lease.repository;

import com.imchobo.lease.domain.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * RefundRepository
 * - tbl_refund 테이블 접근 전용 DAO
 * - 환불 내역을 관리 (전체/부분 환불 모두 기록)
 */
public interface RefundRepository extends JpaRepository<Refund, Long> {
  // 기본 CRUD 외 특별한 쿼리는 아직 필요 없음
}
