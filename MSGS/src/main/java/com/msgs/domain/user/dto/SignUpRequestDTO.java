package com.msgs.domain.user.dto;

import static com.msgs.global.common.error.ErrorCode.USERTYPE_VALIDATION;

import com.msgs.domain.user.domain.User;
import com.msgs.domain.user.domain.UserType;
import com.msgs.global.common.error.BusinessException;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class SignUpRequestDTO {

  @NotNull(message = "회원 타입은 필수 값입니다.")
  private String userType;

  @NotBlank(message = "이메일을 입력해 주세요.")
  @Email(message = "이메일 형식이 올바르지 않습니다.")
  private String email;

  @NotBlank(message = "전화번호를 입력해 주세요.")
  @Pattern(regexp = "^01([0|1|6|7|8|9])(\\d{3}|\\d{4})\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
  private String phone;

  @Pattern(regexp = "^[A-Za-zㄱ-힣]{2,10}$", message = "닉네임 형식이 올바르지 않습니다.")
  // 영문자 또는 한글로 시작
  // 닉네임이 2자에서 10자 사이
  private String nickname;

  @NotBlank(message = "비밀번호를 입력해 주세요.")
  @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[!@#$%^&*()-_+=])(?=.*\\d).{8,20}$", message = "비밀번호 형식이 올바르지 않습니다.")
  // 적어도 하나 이상의 영문자, 특수문자, 숫자가 포함
  // 비밀번호가 8자 이상 20자 이하
  private String password;

  @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
  private String confirmPassword;

  @Builder.Default
  private String role = "USER";

  public User toEntity() {
    return User.builder()
               .status(userType.substring(0, 1).toUpperCase())
               .userType(UserType.valueOf(userType.toUpperCase()))
               .email(email)
               .phone(phone)
               .nickname(nickname)
               .password(password)
               .role(role)
               .build();
  }

  public boolean validateSignUpRequuest() {
    try {
      validateUserType();
      isPasswordConfirmed();
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  private void validateUserType() {
    if (!UserType.isValidUserType(userType)) {
      throw new BusinessException(USERTYPE_VALIDATION);
    }
  }

  @AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
  private boolean isPasswordConfirmed() {
    return password.equals(confirmPassword);
  }
}