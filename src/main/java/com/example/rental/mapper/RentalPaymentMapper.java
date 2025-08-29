package com.example.rental.mapper;

import com.example.rental.domain.RentalPayment;
import com.example.rental.domain.dto.RentalPaymentDTO;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;


@Mapper(componentModel = "spring")
public interface RentalPaymentMapper {

  // Entity -> DTO
  RentalPaymentDTO toDto(RentalPayment entity);
  List<RentalPaymentDTO> toDtoList(List<RentalPayment> entities);

  // DTO -> Entity (신규 생성용)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)   // @PrePersist에서 자동 세팅
  @Mapping(target = "updatedAt", ignore = true)   // @PrePersist/@PreUpdate에서 자동 세팅
  @Mapping(target = "requestedAt", ignore = true) // 자동 세팅
  @Mapping(target = "paidAt", ignore = true)
  @Mapping(target = "canceledAt", ignore = true)
  @Mapping(target = "refundedAt", ignore = true)
  RentalPayment toEntity(RentalPaymentDTO dto);

  // 부분 업데이트 (PATCH) — null 값은 무시
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDto(RentalPaymentDTO dto, @MappingTarget RentalPayment entity);
}