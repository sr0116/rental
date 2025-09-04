package com.imchobo.lease.service;

import com.imchobo.lease.domain.dto.OrderDTO;

public interface OrderService {
  OrderDTO create(OrderDTO dto);
  OrderDTO updateStatus(Long orderId, String status);
}
