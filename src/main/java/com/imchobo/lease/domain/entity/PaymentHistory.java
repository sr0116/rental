package com.imchobo.lease.domain.entity;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * PaymentHistory 엔티티
 * - 결제 상태 변경 이력을 기록
 * - 예: PENDING → PAID, PAID → REFUNDED 등
 */
@Entity
@Table(name = "tbl_payment_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistory {

  /** 로그 PK */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long paymentHistoryId;

  /** 결제 FK */
  @Column(nullable = false)
  private Long paymentId;

  /** 변경된 결제 상태 */
  @Column(length = 20, nullable = false)
  private String status;

  /** 사유 코드 (실패 코드, 환불 코드 등) */
  @Column(length = 50)
  private String reasonCode;

  /** 상세 사유 메시지 */
  private String reasonMessage;

  /** 변경 주체 (USER / ADMIN / SYSTEM) */
  @Column(length = 20, nullable = false)
  private String actorType;

  /** 변경 주체 ID (회원/관리자 FK) */
  private Long actorId;

  /** 로그 생성 시각 */
  @Column(columnDefinition = "datetime default now()")
  private LocalDateTime regdate;
}
