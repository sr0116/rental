package com.imchobo.lease.repository;

import com.imchobo.lease.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
  Optional<Payment> findByMerchantUid(String merchantUid);
}
