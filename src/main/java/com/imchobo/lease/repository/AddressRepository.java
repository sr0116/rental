package com.imchobo.lease.repository;

import com.imchobo.lease.domain.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

  List<Address> findAllByMnoOrderByRegdateDesc(Long mno);

  Optional<Address> findByAddrnoAndMno(Long addrno, Long mno);

  @Modifying
  @Query("update Address a set a.isDefault = false where a.mno = :mno")
  int clearDefaultByMno(@Param("mno") Long mno);
}

