package com.msgs.global.common.error;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

  String name();  // Enum에서 name(): Enum 상수 반환

  HttpStatus getHttpStatus(); // Error HTTP Status

  String getMessage(); // Error Message
}
