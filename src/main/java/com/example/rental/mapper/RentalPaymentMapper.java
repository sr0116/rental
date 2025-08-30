package com.example.rental.mapper;

import com.example.rental.domain.RentalPayment;
import com.example.rental.domain.dto.RentalPaymentDTO;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;


@Mapper(componentModel = "spring")
public interface RentalPaymentMapper {
  RentalPaymentDTO toDto(RentalPayment entity);
  List<RentalPaymentDTO> toDtoList(List<RentalPayment> entities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "requestedAt", ignore = true)
  @Mapping(target = "paidAt", ignore = true)
  @Mapping(target = "canceledAt", ignore = true)
  @Mapping(target = "refundedAt", ignore = true)
  @Mapping(target = "isRefunded", ignore = true)
  RentalPayment toEntity(RentalPaymentDTO dto);



  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromDto(RentalPaymentDTO dto, @MappingTarget RentalPayment entity);
}