package com.example.rental.domain;


import lombok.*;

// 공통 응답
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
  private boolean ok;
  private String message;
  private T data;
  public static <T> ApiResponse<T> ok(T data){
    return new ApiResponse<>(true, null, data);
  }
  public static <T> ApiResponse<T> fail(String message){
    return new ApiResponse<>(false, message, null);
  }
}