package com.imchobo.lease.domain.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressRequest {
  private Long mno;
  private String name;
  private String tel;
  private String zipcode;
  private String addr;
  private String addrDetail;
  private Boolean isDefault;
  private String memo;
}

