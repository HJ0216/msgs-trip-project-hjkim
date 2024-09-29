package com.msgs.global.common.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.FieldError;

@Getter
@Builder
@RequiredArgsConstructor
public class ErrorResponse {

  private final String code;
  private final String message;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  // errors 필드가 비어 있지 않을 때만 JSON 직렬화에 포함
  private final List<ValidationError> errors;
  // 클라이언트 요청에서 특정 필드에 잘못된 데이터가 들어온 경우 유효성 검사 오류 저장

  @Getter
  @Builder
  @RequiredArgsConstructor
  public static class ValidationError {
    // @Valid를 사용했을 때 에러가 발생한 경우 어느 필드에서 에러가 발생했는지 응답을 위한 내부 정적 클래스

    private final String field;
    private final String message;

    public static ValidationError of(final FieldError fieldError) {
      // FieldError: Spring에서 유효성 검사가 실패한 필드에 대한 정보를 제공하는 클래스
      return ValidationError.builder()
                            .field(fieldError.getField())
                            .message(fieldError.getDefaultMessage())
                            .build();
    }
  }
}