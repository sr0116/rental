package com.imchobo.lease.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_subscribe")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscribe {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long subscribeId;

  @Column(nullable = false)
  private Long orderId;

  @Column(nullable = false)
  private Long memberId;

  @Column(nullable = false, length = 20)
  private String status;  // WAITING / ACTIVE / CANCELED / ENDED

  private Integer depositSnapshot;
  private Integer monthlyFeeSnapshot;

  @Column(columnDefinition = "datetime default now()")
  private LocalDateTime regdate;
}
