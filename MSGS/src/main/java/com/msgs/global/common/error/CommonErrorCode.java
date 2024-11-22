package com.msgs.global.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
  UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
      "지원하지 않는 Content-Type입니다. application/json 형식으로 요청해주세요."),
  METHOD_NOT_SUPPORTED(HttpStatus.METHOD_NOT_ALLOWED,
      "지원하지 않는 HTTP 메서드입니다. 요청 메서드 타입을 다시 확인해주세요."),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),

  // Redis
  REDIS_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis 서버 연결에 실패했습니다. 잠시 후 다시 시도해주세요."),
  REDIS_OPERATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Redis 작업 중 오류가 발생했습니다."),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
