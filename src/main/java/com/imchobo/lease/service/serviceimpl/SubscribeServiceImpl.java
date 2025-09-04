package com.imchobo.lease.service.serviceimpl;

import com.imchobo.lease.domain.dto.SubscribeDTO;
import com.imchobo.lease.domain.entity.Subscribe;
import com.imchobo.lease.mapper.SubscribeMapper;
import com.imchobo.lease.repository.SubscribeRepository;
import com.imchobo.lease.service.SubscribeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscribeServiceImpl implements SubscribeService {

  private final SubscribeRepository subscribeRepository;
  private final SubscribeMapper subscribeMapper;

  @Override
  @Transactional
  public SubscribeDTO create(SubscribeDTO dto) {
    Subscribe entity = subscribeMapper.toEntity(dto);
    entity.setStatus("WAITING");
    return subscribeMapper.toDTO(subscribeRepository.save(entity));
  }

  @Override
  @Transactional
  public SubscribeDTO updateStatus(Long subscribeId, String status) {
    Subscribe subscribe = subscribeRepository.findById(subscribeId)
            .orElseThrow(() -> new IllegalArgumentException("구독을 찾을 수 없습니다."));
    subscribe.setStatus(status);
    return subscribeMapper.toDTO(subscribeRepository.save(subscribe));
  }
}
