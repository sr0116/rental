package com.imchobo.lease.service.serviceimpl;

import com.imchobo.lease.domain.dto.OrderDTO;
import com.imchobo.lease.domain.entity.Order;
import com.imchobo.lease.mapper.OrderMapper;
import com.imchobo.lease.repository.OrderRepository;
import com.imchobo.lease.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final OrderMapper orderMapper;

  @Override
  @Transactional
  public OrderDTO create(OrderDTO dto) {
    Order entity = orderMapper.toEntity(dto);
    entity.setStatus("PENDING");
    return orderMapper.toDTO(orderRepository.save(entity));
  }

  @Override
  @Transactional
  public OrderDTO updateStatus(Long orderId, String status) {
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
    order.setStatus(status);
    return orderMapper.toDTO(orderRepository.save(order));
  }
}
