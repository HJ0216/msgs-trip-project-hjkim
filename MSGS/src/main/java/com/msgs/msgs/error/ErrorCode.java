package com.msgs.msgs.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum ErrorCode {

    // 회원
    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일 입니다."),
    NOT_EQUAL_PASSWORD(HttpStatus.BAD_REQUEST,"입력한 비밀번호가 상이합니다."),
    CHECK_LOGIN_ID_OR_PASSWORD(HttpStatus.NOT_FOUND, "아이디 또는 비밀번호를 확인해주세요."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    LOGOUT_MEMBER(HttpStatus.NOT_FOUND, "로그아웃한 회원입니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.NOT_FOUND, "유효하지 않은 Access Token입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "유효하지 않은 Refresh Token입니다."),

    // 권한
    NOT_FOUND_AUTHORITY(HttpStatus.NOT_FOUND, "존재하지 않는 권한입니다."),
    ;


    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

/*    public static Optional<ErrorCode> valueOfHttpStatus(HttpStatus httpStatus){
        return Arrays.stream(values())
                .filter(value -> value.httpStatus.equals(httpStatus))
                .findAny();
    }*/

    // HttpStatus -> ErrorCode 조회
    private static final Map<HttpStatus, ErrorCode> BY_HTTPSTATUS =
            Stream.of(values()).collect(Collectors.toMap(ErrorCode::getHttpStatus, e -> e, (e1, e2) -> e1));

    public static Optional<ErrorCode> valueOfHttpStatus(HttpStatus httpStatus){
        return Optional.ofNullable(BY_HTTPSTATUS.get(httpStatus));
    }

    // Message -> ErrorCode 조회
    private static final Map<String, ErrorCode> BY_MESSAGE =
            Stream.of(values()).collect(Collectors.toMap(ErrorCode::getMessage, e -> e));

    public static Optional<ErrorCode> valueOfMessage(String message){
        return Optional.ofNullable(BY_MESSAGE.get(message));
    }

}
