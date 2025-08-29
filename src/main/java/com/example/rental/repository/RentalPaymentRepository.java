package com.example.rental.repository;

import com.example.rental.domain.RentalPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RentalPaymentRepository extends JpaRepository<RentalPayment, Long> {
  Optional<RentalPayment> findByMerchantUid(String merchantUid);
}
