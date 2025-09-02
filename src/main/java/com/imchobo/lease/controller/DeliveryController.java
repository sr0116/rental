package com.imchobo.lease.controller;

import com.imchobo.lease.config.ApiResponse;
import com.imchobo.lease.domain.DeliveryCreateRequest;
import com.imchobo.lease.domain.DeliveryResponse;
import com.imchobo.lease.domain.dto.DeliveryStatusUpdateRequest;
import com.imchobo.lease.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

  private final DeliveryService service;

  /**
   * 배송 요청 생성
   * POST /api/deliveries
   */
  @PostMapping
  public ApiResponse<DeliveryResponse> create(@RequestBody DeliveryCreateRequest req) {
    return ApiResponse.ok(service.create(req));
  }

  /**
   * 배송 상태 변경
   * PATCH /api/deliveries/{dno}/status
   */
  @PatchMapping("/{dno}/status")
  public ApiResponse<DeliveryResponse> updateStatus(@PathVariable Long dno,
                                                    @RequestBody DeliveryStatusUpdateRequest req) {
    return ApiResponse.ok(service.updateStatus(dno, req));
  }

  /**
   * 회원별 배송 목록 조회
   * GET /api/deliveries?mno=1
   */
  @GetMapping
  public ApiResponse<List<DeliveryResponse>> list(@RequestParam Long mno) {
    return ApiResponse.ok(service.list(mno));
  }
}
