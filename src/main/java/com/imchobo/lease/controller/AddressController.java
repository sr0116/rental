package com.imchobo.lease.controller;

import com.imchobo.lease.config.ApiResponse;
import com.imchobo.lease.domain.AddressResponse;
import com.imchobo.lease.domain.dto.AddressRequest;
import com.imchobo.lease.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
  private final AddressService service;

  /**
   * 배송지 등록
   * POST /api/addresses
   */
  @PostMapping
  public ApiResponse<AddressResponse> create(@RequestBody AddressRequest req) {
    return ApiResponse.ok(service.create(req));
  }

  /**
   * 회원 배송지 목록 조회
   * GET /api/addresses?mno=1
   */
  @GetMapping
  public ApiResponse<List<AddressResponse>> list(@RequestParam Long mno) {
    return ApiResponse.ok(service.list(mno));
  }

  /**
   * 기본 배송지 지정
   * PATCH /api/addresses/{addrno}/default?mno=1
   */
  @PatchMapping("/{addrno}/default")
  public ApiResponse<AddressResponse> setDefault(@PathVariable Long addrno,
                                                 @RequestParam Long mno) {
    return ApiResponse.ok(service.setDefault(mno, addrno));
  }
}