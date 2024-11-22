package com.msgs.domain.user.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateUserPasswordRequestDTO {

  private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*[!@#$%^&*()-_+=])(?=.*\\d).{8,20}$";

  @NotBlank(message = "비밀번호를 입력해 주세요.")
  @Pattern(regexp = PASSWORD_REGEX, message = "비밀번호는 8~20자의 영문자, 특수문자, 숫자를 포함해야 합니다.")
  private String password;

  @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
  private String confirmPassword;

  @AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
  private boolean isValidPasswordConfirmed() {
    return password.equals(confirmPassword);
  }
}
