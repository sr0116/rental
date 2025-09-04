package com.imchobo.lease.controller;

import com.imchobo.lease.config.ApiResponse;
import com.imchobo.lease.domain.dto.OrderDTO;
import com.imchobo.lease.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  public ApiResponse<OrderDTO> create(@RequestBody OrderDTO dto) {
    return ApiResponse.ok(orderService.create(dto));
  }

  @PatchMapping("/{id}/status")
  public ApiResponse<OrderDTO> updateStatus(@PathVariable Long id, @RequestParam String status) {
    return ApiResponse.ok(orderService.updateStatus(id, status));
  }
}
