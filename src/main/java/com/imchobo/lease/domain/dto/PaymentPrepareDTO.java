package com.imchobo.lease.domain.dto;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 결제 준비 요청 DTO
 * - 사용자가 결제 버튼 클릭 시 서버로 전달되는 정보
 * - PortOne 사전 등록에 필요한 최소한의 값
 */

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentPrepareDTO {
  private Long orderId;
  private BigDecimal amount;
}