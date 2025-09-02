package com.imchobo.lease.repository;

import com.imchobo.lease.domain.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery,Long> {
  List<Delivery> findAllByMnoOrderByRegdateDesc(Long mno);
}
