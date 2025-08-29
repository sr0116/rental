package com.example.rental.service;

import com.example.rental.domain.dto.*;

import java.util.List;

public interface PaymentService {
  ApiResponse<Void> prepare(PaymentPrepareRequest req);
  ApiResponse<Object> complete(PaymentCompleteRequest req);
  ApiResponse<Object> refund(RefundRequest req);

  RentalPaymentDTO getByMerchantUid(String merchantUid);
  List<RentalPaymentDTO> getRecent(int size);
}
