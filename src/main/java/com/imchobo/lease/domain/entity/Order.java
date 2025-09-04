package com.imchobo.lease.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_order")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long orderId;

  @Column(nullable = false)
  private Long memberId;

  @Column(nullable = false)
  private Long addrId;

  @Column(nullable = false, length = 20)
  private String status; // PENDING / PAID / SHIPPED / DELIVERED / CANCELED

  @Column(columnDefinition = "datetime default now()")
  private LocalDateTime regdate;

  private LocalDateTime moddate;
}