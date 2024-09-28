package com.msgs.global.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
  BAD_REQUEST(HttpStatus.BAD_REQUEST, "Invalid parameter included"),
  NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not exists"),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
