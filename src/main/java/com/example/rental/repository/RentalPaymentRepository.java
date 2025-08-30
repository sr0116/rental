package com.example.rental.repository;

import com.example.rental.domain.RentalPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RentalPaymentRepository extends JpaRepository<RentalPayment, Long> {

  Optional<RentalPayment> findByMerchantUid(String merchantUid);

  // 특정 유저의 결제 내역 (최근 순)
  List<RentalPayment> findByUserEmailOrderByPaidAtDesc(String userEmail);

  // BillingKey 기반 최신 정기결제 건 조회
  Optional<RentalPayment> findTopByCustomerUidAndPlanTypeOrderByPaidAtDesc(
          String customerUid,
          RentalPayment.PlanType planType
  );

  // 환불 가능한 정기결제 내역 조회
  Optional<RentalPayment> findByIdAndPlanTypeAndIsRefundedFalse(
          Long id,
          RentalPayment.PlanType planType
  );
}

