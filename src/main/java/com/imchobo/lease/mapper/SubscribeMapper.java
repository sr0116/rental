package com.imchobo.lease.mapper;

import com.imchobo.lease.domain.dto.SubscribeDTO;
import com.imchobo.lease.domain.entity.Subscribe;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscribeMapper {
  SubscribeDTO toDTO(Subscribe entity);
  Subscribe toEntity(SubscribeDTO dto);
}
