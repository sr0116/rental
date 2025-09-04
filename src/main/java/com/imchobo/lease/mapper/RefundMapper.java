package com.imchobo.lease.mapper;


import com.imchobo.lease.domain.dto.RefundRequestDTO;
import com.imchobo.lease.domain.entity.Refund;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

/**
 * RefundMapper
 * - 환불 요청 DTO ↔ Refund 엔티티 매핑
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)  // 매핑 누락 무시
public interface RefundMapper {

  @Mapping(target = "refundId", ignore = true) // PK는 DB에서 자동 생성
  @Mapping(target = "regdate", expression = "java(java.time.LocalDateTime.now())")
  Refund toEntity(RefundRequestDTO dto);
}
