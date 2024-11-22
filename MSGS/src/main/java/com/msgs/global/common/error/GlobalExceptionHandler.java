package com.msgs.global.common.error;

import com.msgs.domain.user.exception.UserErrorCode;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public static ResponseEntity<Object> handleBusiness(BusinessException e) {
    log.error("Business Exception: {}", e.getMessage());

    ErrorCode errorCode = e.getErrorCode();
    return handleExceptionInternal(errorCode);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
    log.error("IllegalArgument Exception: {}", e.getMessage());

    ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
    return handleExceptionInternal(errorCode, e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException e) {
    log.error("Validation Exception: {}", e.getMessage());

    ErrorCode errorCode = CommonErrorCode.INVALID_PARAMETER;
    return handleExceptionInternal(e, errorCode);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<Object> handleAccessDenied(AccessDeniedException e) {
    log.error("Access Denied Exception: {}", e.getMessage());

    ErrorCode errorCode = UserErrorCode.INVALID_ACCESS_TOKEN;
    return handleExceptionInternal(errorCode);
  }

  @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
  public ResponseEntity<Object> handleHttpMediaTypeNotSupported(
      HttpMediaTypeNotSupportedException e) {
    log.error("Unsupported Media Type Exception: {}", e.getMessage());

    ErrorCode errorCode = UserErrorCode.UNSUPPORTED_MEDIA_TYPE;
    return handleExceptionInternal(errorCode);
  }

  @ExceptionHandler({Exception.class})
  public static ResponseEntity<Object> handleAllException(Exception e) {
    log.error("Internal Server Error", e);

    ErrorCode errorCode = CommonErrorCode.INTERNAL_SERVER_ERROR;
    return handleExceptionInternal(errorCode);
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
