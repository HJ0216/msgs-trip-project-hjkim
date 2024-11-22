package com.msgs.global.common.error;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public static ResponseEntity<Object> handleBusiness(BusinessException e) {
    log.error("Business Exception: {}", e.getMessage());

    return handleExceptionInternal(e.getErrorCode());
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException e) {
    log.error("Unsupported Media Type Exception: {}", e.getMessage());

    return handleExceptionInternal(CommonErrorCode.UNSUPPORTED_MEDIA_TYPE);
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<Object> handleHttpRequestMethodNotSupported(
      HttpRequestMethodNotSupportedException e) {
    log.error("Method Not Supported Exception: {}", e.getMessage());

    return handleExceptionInternal(CommonErrorCode.METHOD_NOT_SUPPORTED);
  }

  @ExceptionHandler({Exception.class})
  public static ResponseEntity<Object> handleAllException(Exception e) {
    log.error("Internal Server Error", e);

    return handleExceptionInternal(CommonErrorCode.INTERNAL_SERVER_ERROR);
  }

  private static ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode) {
    return ResponseEntity.status(errorCode.getHttpStatus())
                         .body(makeErrorResponse(errorCode));
  }

  private static ErrorResponse makeErrorResponse(ErrorCode errorCode) {
    return ErrorResponse.builder()
                        .code(errorCode.name())
                        .message(errorCode.getMessage())
                        .build();
  }

  private ResponseEntity<Object> handleExceptionInternal(ErrorCode errorCode, String message) {
    return ResponseEntity.status(errorCode.getHttpStatus())
                         .body(makeErrorResponse(errorCode, message));
  }

  private ErrorResponse makeErrorResponse(ErrorCode errorCode, String message) {
    return ErrorResponse.builder()
                        .code(errorCode.name())
                        .message(message)
                        .build();
  }

  private ResponseEntity<Object> handleExceptionInternal(BindException e, ErrorCode errorCode) {
    return ResponseEntity.status(errorCode.getHttpStatus())
                         .body(makeErrorResponse(e, errorCode));
  }

  private ErrorResponse makeErrorResponse(BindException e, ErrorCode errorCode) {
    List<ErrorResponse.ValidationError> validationErrorList = e.getBindingResult()
                                                               .getFieldErrors()
                                                               .stream()
                                                               .map(
                                                                   ErrorResponse.ValidationError::of)
                                                               .toList();

    return ErrorResponse.builder()
                        .code(errorCode.name())
                        .message(errorCode.getMessage())
                        .errors(validationErrorList)
                        .build();
  }
}
