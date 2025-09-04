package com.imchobo.lease.mapper;

import com.imchobo.lease.domain.dto.OrderDTO;
import com.imchobo.lease.domain.entity.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
  OrderDTO toDTO(Order entity);
  Order toEntity(OrderDTO dto);
}
