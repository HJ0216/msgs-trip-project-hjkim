package com.msgs.domain.user.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateUserNicknameRequestDTO {

  private static final String NICKNAME_REGEX = "^[A-Za-zㄱ-힣0-9]{2,8}$";

  @Pattern(regexp = NICKNAME_REGEX, message = "닉네임은 2~8자의 한글, 영문 대/소문자, 숫자로만 이뤄져야 합니다.")
  private String nickname;

}
