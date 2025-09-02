package com.imchobo.lease.domain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
  private Long addrno;
  private Long mno;
  private String name;
  private String tel;
  private String zipcode;
  private String addr;
  private String addrDetail;
  private Boolean isDefault;
  private String memo;
  private LocalDateTime regdate;
}