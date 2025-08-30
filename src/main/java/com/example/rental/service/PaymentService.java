package com.example.rental.service;

import com.example.rental.domain.ApiResponse;
import com.example.rental.domain.PaymentCompleteRequest;
import com.example.rental.domain.PaymentPrepareRequest;
import com.example.rental.domain.RefundRequest;
import com.example.rental.domain.dto.RentalPaymentDTO;

import java.util.List;

public interface PaymentService {

  ApiResponse<Void> prepare(PaymentPrepareRequest req);

  ApiResponse<Object> complete(PaymentCompleteRequest req);

  ApiResponse<Object> refund(RefundRequest req);

  RentalPaymentDTO getByMerchantUid(String merchantUid);

  List<RentalPaymentDTO> getRecent(int size);

  // 정기 결제 실행
  RentalPaymentDTO chargeRecurring(String billingKey, int amount, String userEmail, int periodDays);

  // 정기 결제 환불
  int refundRecurring(Long paymentId);
}
