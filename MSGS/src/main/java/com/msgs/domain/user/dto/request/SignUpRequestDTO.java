package com.msgs.domain.user.dto.request;

import com.msgs.domain.user.domain.User;
import com.msgs.domain.user.domain.UserType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Builder
@AllArgsConstructor
@Getter
public class SignUpRequestDTO {

  private static final String EMAIL_REGEX = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
  private static final String PHONE_REGEX = "^01([0|1|6|7|8|9])(\\d{3}|\\d{4})\\d{4}$";
  private static final String NICKNAME_REGEX = "^[A-Za-zㄱ-힣0-9]{2,8}$";
  private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*[!@#$%^&*()-_+=])(?=.*\\d).{8,20}$";

  @NotNull(message = "회원 타입은 필수 값입니다.")
  private String userType;

  @NotBlank(message = "이메일을 입력해 주세요.")
  @Pattern(regexp = EMAIL_REGEX, message = "이메일 형식이 올바르지 않습니다.")
  private String email;

  @NotBlank(message = "전화번호를 입력해 주세요.")
  @Pattern(regexp = PHONE_REGEX, message = "전화번호 형식이 올바르지 않습니다.")
  private String phone;

  @Pattern(regexp = NICKNAME_REGEX, message = "닉네임은 2~8자의 한글, 영문 대/소문자, 숫자로만 이뤄져야 합니다.")
  private String nickname;

  @NotBlank(message = "비밀번호를 입력해 주세요.")
  @Pattern(regexp = PASSWORD_REGEX, message = "비밀번호는 8~20자의 영문자, 특수문자, 숫자를 포함해야 합니다.")
  private String password;

  @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
  private String confirmPassword;

  private String role;

  public String getRole() {
    return role == null || role.isEmpty() ? "ROLE_USER" : role;
  }

  public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
    return User.builder()
               .status(userType.substring(0, 1).toUpperCase())
               .userType(UserType.valueOf(userType.toUpperCase()))
               .email(email)
               .phone(phone)
               .nickname(nickname)
               .password(bCryptPasswordEncoder.encode(password))
               .role(getRole())
               .build();
  }

  @AssertTrue(message = "회원 타입이 올바르지 않습니다.")
  private boolean isValidUserType() {
    return UserType.isValidUserType(userType);
  }

  @AssertTrue(message = "비밀번호와 비밀번호 확인이 일치하지 않습니다.")
  private boolean isValidPasswordConfirmed() {
    return password.equals(confirmPassword);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SignUpRequestDTO)) {
      return false;
    }
    SignUpRequestDTO that = (SignUpRequestDTO) o;
    return Objects.equals(userType, that.userType) &&
        Objects.equals(email, that.email) &&
        Objects.equals(phone, that.phone) &&
        Objects.equals(nickname, that.nickname) &&
        Objects.equals(password, that.password) &&
        Objects.equals(confirmPassword, that.confirmPassword);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userType, email, phone, nickname, password, confirmPassword);
  }
}