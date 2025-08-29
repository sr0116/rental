package com.example.rental.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

// 3) 정기결제 재청구
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecurringChargeRequest {
  @NotBlank
  private String customerUid;
  @Positive
  private Integer amount;
  private String name;
  private String userEmail;
  private String itemId;
}
