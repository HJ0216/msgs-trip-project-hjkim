package com.msgs.domain.user.dto;

import com.msgs.domain.user.domain.User;
import com.msgs.global.util.ValidationUtils;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class SignUpRequestDTO {
    @NotEmpty
    private String status;
    @NotEmpty
    private String email;
    private String phone;
//    @Pattern(regexp = "^[가-힣a-zA-Z0-9]{2,8}$", message = "한글, 영문 대소문자, 숫자 가능")
    private String nickname;
    @NotEmpty
    private String password;
    private String confirmPassword;

    public User toEntity(){
        return User.builder()
                .status(status)
                .email(email)
                .phone(phone)
                .nickname(nickname)
                .password(password)
                .build();
    }

    public void validUserDto() {
        ValidationUtils.validateEmail(email);
        ValidationUtils.validatePhoneNumber(phone);
        ValidationUtils.validateName(nickname);
        ValidationUtils.validatePassword(password);
        ValidationUtils.confirmPassword(password, confirmPassword);
    }
}
