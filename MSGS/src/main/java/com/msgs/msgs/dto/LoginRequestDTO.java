package com.msgs.msgs.dto;

import com.msgs.msgs.entity.user.LoginType;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class LoginRequestDTO {
    @NotNull(message = "이메일은 필수값입니다.")
    private String email;

    @NotNull(message = "아이디는 필수값입니다.")
    private Integer id;

    @NotNull(message = "비밀번호는 필수값입니다.")
    private String password;

    @NotNull(message = "로그인타입은 필수값입니다.")
    private LoginType loginType; // GOOGLE, KAKAO, MSGS, NAVER
}
