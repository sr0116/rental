package com.imchobo.lease.service;

import com.imchobo.lease.domain.AddressResponse;
import com.imchobo.lease.domain.dto.AddressRequest;

import java.util.List;

public interface AddressService{
  AddressResponse create(AddressRequest req);
  List<AddressResponse> list(Long mno);
  AddressResponse setDefault(Long mno, Long addrno);
}