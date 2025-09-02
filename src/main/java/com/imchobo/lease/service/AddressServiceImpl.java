package com.imchobo.lease.service;

import com.imchobo.lease.domain.AddressResponse;
import com.imchobo.lease.domain.dto.AddressRequest;
import com.imchobo.lease.domain.entity.Address;
import com.imchobo.lease.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

  private final AddressRepository repo;

  @Transactional
  public AddressResponse create(AddressRequest r) {
    if (Boolean.TRUE.equals(r.getIsDefault())) {
      repo.clearDefaultByMno(r.getMno());
    }

    Address saved = repo.save(Address.builder()
            .mno(r.getMno())
            .name(r.getName())
            .tel(r.getTel())
            .zipcode(r.getZipcode())
            .address(r.getAddr())
            .addressDetail(r.getAddrDetail())
            .isDefault(Boolean.TRUE.equals(r.getIsDefault()))
            .memo(r.getMemo())
            .build());

    return toRes(saved);
  }

  @Transactional(readOnly = true)
  public List<AddressResponse> list(Long mno) {
    return repo.findAllByMnoOrderByRegdateDesc(mno).stream()
            .map(this::toRes)
            .toList();
  }

  @Transactional
  public AddressResponse setDefault(Long mno, Long addrno) {
    Address target = repo.findByAddrnoAndMno(addrno, mno)
            .orElseThrow(() -> new IllegalArgumentException("주소 없음"));

    repo.clearDefaultByMno(mno);
    target.setIsDefault(true); // save() 없어도 Dirty Checking으로 반영됨

    return toRes(target);
  }

  private AddressResponse toRes(Address a) {
    return AddressResponse.builder()
            .addrno(a.getAddrno())
            .mno(a.getMno())
            .name(a.getName())
            .tel(a.getTel())
            .zipcode(a.getZipcode())
            .addr(a.getAddress())
            .addrDetail(a.getAddressDetail())
            .isDefault(a.getIsDefault())
            .memo(a.getMemo())
            .regdate(a.getRegdate())
            .build();
  }
}
