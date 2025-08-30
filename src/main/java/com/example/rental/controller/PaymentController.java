package com.example.rental.controller;

import com.example.rental.domain.ApiResponse;
import com.example.rental.domain.PaymentCompleteRequest;
import com.example.rental.domain.PaymentPrepareRequest;
import com.example.rental.domain.RefundRequest;
import com.example.rental.domain.dto.RecurringChargeRequest;
import com.example.rental.domain.dto.RentalPaymentDTO;
import com.example.rental.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService service;

  @PostMapping("/prepare")
  public ResponseEntity<ApiResponse<Void>> prepare(@RequestBody PaymentPrepareRequest req) {
    return ResponseEntity.ok(service.prepare(req));
  }

  @PostMapping("/complete")
  public ResponseEntity<ApiResponse<Object>> complete(@RequestBody PaymentCompleteRequest req) {
    return ResponseEntity.ok(service.complete(req));
  }

  @PostMapping("/refund")
  public ResponseEntity<ApiResponse<Object>> refund(@RequestBody RefundRequest req) {
    return ResponseEntity.ok(service.refund(req));
  }

  @GetMapping("/{merchantUid}")
  public ResponseEntity<ApiResponse<RentalPaymentDTO>> getOne(@PathVariable String merchantUid) {
    return ResponseEntity.ok(ApiResponse.ok(service.getByMerchantUid(merchantUid)));
  }

  @GetMapping("/recent")
  public ResponseEntity<ApiResponse<List<RentalPaymentDTO>>> recent(
          @RequestParam(defaultValue = "10") int size) {
    return ResponseEntity.ok(ApiResponse.ok(service.getRecent(size)));
  }

  @PostMapping("/recurring/charge")
  public ResponseEntity<ApiResponse<RentalPaymentDTO>> chargeRecurring(@RequestBody RecurringChargeRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(
            service.chargeRecurring(req.getCustomerUid(), req.getAmount(), req.getUserEmail(),
                    req.getPeriodDays() != null ? req.getPeriodDays() : 30)));

  }

  // 정기 결제 환불
  @PostMapping("/recurring/{paymentId}/refund")
  public ResponseEntity<ApiResponse<Integer>> refundRecurring(@PathVariable Long paymentId) {
    return ResponseEntity.ok(ApiResponse.ok(service.refundRecurring(paymentId)));
  }
}
