package com.imchobo.lease.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Refund 엔티티
 * - 부분 환불 / 전체 환불 내역 관리
 * - payment_id 기준으로 결제와 연결
 */
@Entity
@Table(name = "tbl_refund")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Refund {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long refundId;

  @Column(nullable = false)
  private Long paymentId;

  /** 환불 금액 - BigDecimal 사용 */
  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  private String reason;

  @Column(columnDefinition = "datetime default now()")
  private LocalDateTime regdate;
}

