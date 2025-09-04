package com.imchobo.lease.service;

import com.imchobo.lease.domain.dto.SubscribeDTO;

public interface SubscribeService {
  SubscribeDTO create(SubscribeDTO dto);
  SubscribeDTO updateStatus(Long subscribeId, String status);
}
