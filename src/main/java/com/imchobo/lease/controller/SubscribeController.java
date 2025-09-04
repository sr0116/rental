package com.imchobo.lease.controller;

import com.imchobo.lease.config.ApiResponse;
import com.imchobo.lease.domain.dto.SubscribeDTO;
import com.imchobo.lease.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscribe")
@RequiredArgsConstructor
public class SubscribeController {

  private final SubscribeService subscribeService;

  @PostMapping
  public ApiResponse<SubscribeDTO> create(@RequestBody SubscribeDTO dto) {
    return ApiResponse.ok(subscribeService.create(dto));
  }

  @PatchMapping("/{id}/status")
  public ApiResponse<SubscribeDTO> updateStatus(@PathVariable Long id, @RequestParam String status) {
    return ApiResponse.ok(subscribeService.updateStatus(id, status));
  }
}
