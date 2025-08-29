package com.example.rental.controller;

import com.example.rental.domain.dto.*;
import com.example.rental.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService service;

  /** 결제 준비 */
  @PostMapping("/prepare")
  public ResponseEntity<ApiResponse<Void>> prepare(@RequestBody @Valid PaymentPrepareRequest req) {
    return ResponseEntity.ok(service.prepare(req));
  }

  /** 결제 완료 검증 */
  @PostMapping("/complete")
  public ResponseEntity<ApiResponse<Object>> complete(@RequestBody @Valid PaymentCompleteRequest req) {
    return ResponseEntity.ok(service.complete(req));
  }

  /** 환불 */
  @PostMapping("/refund")
  public ResponseEntity<ApiResponse<Object>> refund(@RequestBody @Valid RefundRequest req) {
    return ResponseEntity.ok(service.refund(req));
  }

  /** 단건 조회 */
  @GetMapping("/{merchantUid}")
  public ResponseEntity<ApiResponse<RentalPaymentDTO>> getOne(@PathVariable String merchantUid) {
    return ResponseEntity.ok(ApiResponse.ok(service.getByMerchantUid(merchantUid)));
  }

  /** 최근 조회 */
  @GetMapping("/recent")
  public ResponseEntity<ApiResponse<List<RentalPaymentDTO>>> recent(
          @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(ApiResponse.ok(service.getRecent(size)));
  }
}
