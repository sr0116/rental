package com.imchobo.lease.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_payment")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long paymentId;

  private Long orderId;

  @Column(nullable = false, unique = true, length = 100)
  private String merchantUid;

  @Column(length = 100)
  private String impUid;

  @Column(length = 50)
  private String paytype;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal amount;

  @Column(nullable = false, length = 20)
  private String paystatus;

  private String receipt;

  @Column(columnDefinition = "datetime default now()")
  private LocalDateTime regdate;

  private LocalDateTime voiddate;
}
