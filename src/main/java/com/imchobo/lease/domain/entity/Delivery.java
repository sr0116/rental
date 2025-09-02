package com.imchobo.lease.domain.entity;

import com.imchobo.lease.domain.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;



@Entity
@Table(name = "tbl_delivery")
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class Delivery {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long dno;

  private Long orderId;

  private Long subscribeId;

  private Long mno;
  private Long addrno;
  private Long pno;

  @Column(name = "tracking_no")
  private String trackingNo;

  @Column(name = "carrier_no")
  private String carrierCode;

  private String memo;

  @Enumerated(EnumType.STRING)
  private DeliveryStatus status;

  @Column(name = "shipped_at")
  private LocalDateTime shippedAt;

  @Column(name = "regdate")
  private LocalDateTime regdate;

  @PrePersist
  void prePersist() {
    if (regdate == null) regdate = LocalDateTime.now();
    if (status == null) status = DeliveryStatus.READY;
  }
}