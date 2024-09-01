package com.msgs.main.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.msgs.domain.user.dto.LoginRequestDTO;
import com.msgs.global.common.jwt.TokenInfo;
import com.msgs.domain.user.domain.LoginType;
import com.msgs.domain.user.domain.User;
import com.msgs.global.common.error.BusinessException;
import com.msgs.domain.user.repository.UserRepository;
import com.msgs.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static com.msgs.global.common.error.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class UserServiceTest {
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("회원 가입")
    public void userSignUp() throws Exception {
        // given
        User user = new User();
        user.setStatus("M");
        user.setEmail("test@email.com");
        user.setPhone("01023456789");

        // when
        userService.create(user);

        // then
        assertThat(user).isEqualTo(userRepository.findById(createdId));
    }

    @Test
    @DisplayName("이메일이 동일한 회원이 존재할 경우, 예외가 발생한다.")
    public void emailDuplicateCheck() throws Exception {
        // given
        User userA = new User();
        userA.setStatus("M");
        userA.setEmail("");
        userA.setPhone("01023456789");

        String existingEmail = "test@email.com";

        // when
        userService.create(userA);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> userService.emailDuplicateCheck(existingEmail));

        // then
        assertEquals(DUPLICATED_EMAIL, exception.getErrorCode());
    }

    @Test
    @DisplayName("카카오 로그인")
    void kakaoLogin() throws JsonProcessingException {
        //given
        User user = new User();
        user.setStatus("K");
        user.setLoginType(LoginType.KAKAO);
        user.setEmail("test@email.com");
        user.setPhone("01023456789");

        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@email.com", 1, "1234", LoginType.KAKAO);

        //when
        TokenInfo returnTokenInfo = userService.login(loginRequestDTO);

        //then
        assertThat(returnTokenInfo).isNotNull();
        assertThat(returnTokenInfo.getGrantType()).isEqualTo("Bearer");
        assertThat(returnTokenInfo.getAccessToken()).isNotEmpty();
        assertThat(returnTokenInfo.getRefreshToken()).isNotEmpty();
    }
}
