package com.imchobo.lease.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
  @Id
  @GeneratedValue(strategy= GenerationType.IDENTITY) private Long addrno;
  private Long mno; private String name; private String tel; private String zipcode;
  @Column(name="addr") private String address;
  @Column(name="addr_detail") private String addressDetail;
  @Column(name="`default`") private Boolean isDefault;
  private String memo; private LocalDateTime regdate;
  @PrePersist
  void pre(){
    if(isDefault==null)
      isDefault=false;
    if(regdate==null)
      regdate=LocalDateTime.now(); }
}







