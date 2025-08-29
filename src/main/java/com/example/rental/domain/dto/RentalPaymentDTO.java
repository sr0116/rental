package com.example.rental.domain.dto;


import lombok.*;
import com.example.rental.domain.RentalPayment.PayMethod;
import com.example.rental.domain.RentalPayment.PayStatus;
import com.example.rental.domain.RentalPayment.PlanType;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalPaymentDTO {

  private Long id;
  private String merchantUid;
  private String impUid;
  private String customerUid;
  private String pgTid;

  private String userEmail;
  private String itemId;
  private String openId;

  private PlanType planType;
  private PayMethod payMethod;
  private String currency;
  private Integer amount;
  private Integer paidAmount;
  private Integer refundAmount;

  private PayStatus status;
  private String failReason;

  private LocalDateTime requestedAt;
  private LocalDateTime paidAt;
  private LocalDateTime canceledAt;
  private LocalDateTime refundedAt;

  private String webhookPayload;
  private String extraMeta;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}