package com.imchobo.lease.mapper;


import com.imchobo.lease.domain.dto.PaymentCompleteDTO;
import com.imchobo.lease.domain.dto.PaymentDTO;
import com.imchobo.lease.domain.dto.PaymentPrepareDTO;
import com.imchobo.lease.domain.entity.Payment;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentMapper {

  /**
   * 결제 준비: PaymentPrepareDTO → Payment
   * - PENDING 상태 기본값 세팅
   * - merchantUid, impUid, receipt, voiddate 는 Service에서 채움
   */
  @Mapping(target = "paymentId", ignore = true)   // DB에서 자동 생성
  @Mapping(target = "merchantUid", ignore = true) // ServiceImpl에서 PortOne 사전등록 후 세팅
  @Mapping(target = "impUid", ignore = true)      // 결제 완료 시점에만 세팅
  @Mapping(target = "paytype", ignore = true)     // 결제 수단은 결제 완료 후 채움
  @Mapping(target = "receipt", ignore = true)     // 영수증 URL은 외부 응답에서 세팅
  @Mapping(target = "voiddate", ignore = true)    // 환불 시점에만 세팅
  @Mapping(target = "paystatus", constant = "PENDING") // 초기 상태
  @Mapping(target = "regdate", expression = "java(java.time.LocalDateTime.now())")
  Payment toEntity(PaymentPrepareDTO dto);

  /**
   * 결제 완료 업데이트: PaymentCompleteDTO → Payment (일부 필드만 업데이트)
   */
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  @Mapping(target = "paymentId", ignore = true)
  @Mapping(target = "orderId", ignore = true)
  @Mapping(target = "amount", ignore = true)
  @Mapping(target = "paytype", ignore = true)
  @Mapping(target = "paystatus", ignore = true)
  @Mapping(target = "receipt", ignore = true)
  @Mapping(target = "regdate", ignore = true)
  @Mapping(target = "voiddate", ignore = true)
  void updateFromCompleteDto(PaymentCompleteDTO dto, @MappingTarget Payment entity);

  /**
   * Entity → DTO 변환: Payment → PaymentDTO
   * - PaymentDTO는 Entity와 필드를 최대한 동일하게 맞췄으므로 자동 매핑
   */
  PaymentDTO toDto(Payment entity);
}

