package com.imchobo.lease.service;

import com.imchobo.lease.domain.DeliveryCreateRequest;
import com.imchobo.lease.domain.DeliveryResponse;
import com.imchobo.lease.domain.DeliveryStatus;
import com.imchobo.lease.domain.dto.DeliveryStatusUpdateRequest;
import com.imchobo.lease.domain.entity.Delivery;
import com.imchobo.lease.repository.AddressRepository;
import com.imchobo.lease.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

  private final DeliveryRepository repo;
  private final AddressRepository addrRepo;

  /**
   * 배송 생성
   * - 회원 주소 소유 검증
   * - 송장번호 임시 생성
   * - 상태 기본 READY
   */
  @Transactional
  public DeliveryResponse create(DeliveryCreateRequest r) {
    addrRepo.findByAddrnoAndMno(r.getAddrno(), r.getMno())
            .orElseThrow(() -> new IllegalArgumentException("회원 주소 아님/없음"));

    String tracking = "TRK-" + System.currentTimeMillis();

    Delivery d = repo.save(Delivery.builder()
            .orderId(r.getOrderId())
            .subscribeId(r.getSubscribeId())
            .mno(r.getMno())
            .addrno(r.getAddrno())
            .pno(r.getPno())
            .trackingNo(tracking)
            .carrierCode(r.getCarrierCode())
            .memo(r.getMemo())
            .status(DeliveryStatus.READY)
            .build());

    return toRes(d);
  }

  /**
   * 배송 상태 업데이트
   * - READY → PREPARING → SHIPPING → DELIVERED 순서만 허용
   */
  @Transactional
  public DeliveryResponse updateStatus(Long dno, DeliveryStatusUpdateRequest r) {
    Delivery d = repo.findById(dno)
            .orElseThrow(() -> new IllegalArgumentException("배송 없음"));

    if (!canTransit(d.getStatus(), r.getStatus())) {
      throw new IllegalStateException("허용되지 않는 전이: " + d.getStatus() + " -> " + r.getStatus());
    }

    d.setStatus(r.getStatus());

    // SHIPPING 으로 전환되면 발송시각 기록
    if (r.getStatus() == DeliveryStatus.SHIPPING && d.getShippedAt() == null) {
      d.setShippedAt(LocalDateTime.now());
    }

    return toRes(d);
  }

  /**
   * 회원별 배송 목록 조회
   */
  @Transactional(readOnly = true)
  public List<DeliveryResponse> list(Long mno) {
    return repo.findAllByMnoOrderByRegdateDesc(mno).stream()
            .map(this::toRes)
            .toList();
  }

  /**
   * 상태 전이 검증
   */
  private boolean canTransit(DeliveryStatus from, DeliveryStatus to) {
    return switch (from) {
      case READY -> to == DeliveryStatus.PREPARING;
      case PREPARING -> to == DeliveryStatus.SHIPPING;
      case SHIPPING -> to == DeliveryStatus.DELIVERED;
      case DELIVERED -> false;
    };
  }

  /**
   * 엔티티 → 응답 DTO 변환
   */
  private DeliveryResponse toRes(Delivery d) {
    return DeliveryResponse.builder()
            .dno(d.getDno())
            .mno(d.getMno())
            .pno(d.getPno())
            .addrno(d.getAddrno())
            .trackingNo(d.getTrackingNo())
            .carrierCode(d.getCarrierCode())
            .memo(d.getMemo())
            .status(d.getStatus())
            .shippedAt(d.getShippedAt())
            .regdate(d.getRegdate())
            .build();
  }
}
