package com.msgs.global.common.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
public class ErrorResponse {

  private final String errorMessage;

  @Builder
  public ErrorResponse(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public static ResponseEntity<ErrorResponse> toResponseEntity(ErrorCode errorCode) {
    return ResponseEntity.status(errorCode.getHttpStatus())
                         .body(ErrorResponse.builder()
                                            .errorMessage(errorCode.getMessage())
                                            .build());

  }
}