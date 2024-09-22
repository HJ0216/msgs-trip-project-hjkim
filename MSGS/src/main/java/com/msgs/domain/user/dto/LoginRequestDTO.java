package com.msgs.domain.user.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class LoginRequestDTO {

  @NotEmpty(message = "이메일은 필수값입니다.")
  private String email;

  @NotEmpty(message = "비밀번호는 필수값입니다.")
  private String password;

//    @NotNull(message = "Role 타입은 필수값입니다.")
//    private String role;
}
