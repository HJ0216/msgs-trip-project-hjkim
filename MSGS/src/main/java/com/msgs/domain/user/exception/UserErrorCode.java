package com.msgs.domain.user.exception;

import com.msgs.global.common.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
  // 회원 가입
  EMAIL_VALIDATION(HttpStatus.BAD_REQUEST, "이메일 형식이 맞지 않습니다."),
  DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일 입니다."),
  PASSWORD_VALIDATION(HttpStatus.BAD_REQUEST, "비밀번호 형식이 맞지 않습니다."),
  PASSWORD_CONFIRM_VALIDATION(HttpStatus.BAD_REQUEST, "비밀번호와 비밀번호 확인이 맞지 않습니다."),
  PHONE_NUMBER_VALIDATION(HttpStatus.BAD_REQUEST, "휴대폰 형식이 맞지 않습니다."),
  NICKNAME_VALIDATION(HttpStatus.BAD_REQUEST, "닉네임 형식이 맞지 않습니다."),
  USERTYPE_VALIDATION(HttpStatus.BAD_REQUEST, "회원 타입이 올바르지 않습니다."),

  // 로그인
  CHECK_LOGIN_ID_OR_PASSWORD(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호를 확인해주세요."),
  NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),

  // JWT
  MALFORMED_JWT(HttpStatus.BAD_REQUEST, "잘못된 JWT 형식입니다."),
  EXPIRED_JWT(HttpStatus.BAD_REQUEST, "만료된 JWT 토큰입니다."),
  UNSUPPORTED_JWT(HttpStatus.BAD_REQUEST, "지원하지 않은 JWT 서명입니다."),
  ILLEGAL_STATE_JWT(HttpStatus.BAD_REQUEST, "JWT 처리 중 오류가 발생하였습니다."),
  VALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "Access Token의 유효기간이 남아있습니다."),
  LOGOUT_MEMBER(HttpStatus.NOT_FOUND, "로그아웃한 회원입니다."),
  INVALID_ACCESS_TOKEN(HttpStatus.NOT_FOUND, "유효하지 않은 Access Token입니다."),
  INVALID_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "유효하지 않은 Refresh Token입니다."),
  NOT_FOUND_AUTHORITY(HttpStatus.NOT_FOUND, "존재하지 않는 권한입니다."),
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
