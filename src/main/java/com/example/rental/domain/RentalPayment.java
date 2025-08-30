package com.example.rental.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rental_payment")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RentalPayment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String merchantUid;
  private String impUid;
  private String customerUid;
  private String pgTid;

  private String userEmail;
  private String itemId;
  private String openId;

  @Enumerated(EnumType.STRING)
  private PlanType planType;

  @Enumerated(EnumType.STRING)
  private PayMethod payMethod;

  private String currency;
  private Integer amount;
  private Integer paidAmount;
  private Integer refundAmount;

  @Enumerated(EnumType.STRING)
  private PayStatus status;

  private String failReason;

  private LocalDateTime requestedAt;
  private LocalDateTime paidAt;
  private LocalDateTime canceledAt;
  private LocalDateTime refundedAt;

  //  정기 결제 (빌링 키 발급 위해 추가
  private Integer periodDays;      // 이번 정기 결제의 이용일수(예: 30일, 31일)
  private LocalDateTime serviceEndAt; // 이 결제가 보장하는 서비스 종료일

  // 환불 여부 flag (있으면 편함)
  private Boolean isRefunded;

  @Lob
  private String webhookPayload;

  @Lob
  private String extraMeta;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @PrePersist
  public void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    createdAt = now;
    updatedAt = now;
    if (requestedAt == null) {
      requestedAt = now;
    }
    if (currency == null) {
      currency = "KRW";
    }
    if (status == null) {
      status = PayStatus.PENDING;
    }
    if (planType == null) {
      planType = PlanType.ONE_TIME;
    }
  }

  @PreUpdate
  public void preUpdate() {
    updatedAt = LocalDateTime.now();
  }
  //  환불 정책 계산에 필요


  public enum PlanType { ONE_TIME, RECURRING }
  public enum PayMethod { card, vbank, easy, trans, phone }
  public enum PayStatus { PENDING, PAID, FAILED, CANCELED, REFUND_REQUESTED, REFUNDED }
}