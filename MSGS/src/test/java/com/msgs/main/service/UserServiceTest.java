package com.msgs.main.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.msgs.domain.user.dto.LoginRequestDTO;
import com.msgs.domain.user.dto.SignUpRequestDTO;
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
//    @Autowired
//    UserService userService;
//    @Autowired
//    UserRepository userRepository;
//
//    @Test
//    @DisplayName("회원 가입")
//    public void userSignUp() throws Exception {
//        // given
//        SignUpRequestDTO dto = SignUpRequestDTO.builder()
//                .status("M")
//                .email("test@email.com")
//                .phone("01023456789")
//                .nickname("hello")
//                .password("1234")
//                .build();
//
//        // when
//        userService.create(dto);
//
//        // then
//        assertThat(dto.toEntity()).isEqualTo(userRepository.findByEmail(dto.getEmail()));
//    }
//
//    @Test
//    @DisplayName("이메일이 동일한 회원이 존재할 경우, 예외가 발생한다.")
//    public void emailDuplicateCheck() throws Exception {
//        // given
//        SignUpRequestDTO dto = SignUpRequestDTO.builder()
//                .status("M")
//                .email("test@email.com")
//                .phone("01023456789")
//                .nickname("hello")
//                .password("1234")
//                .build();
//
//        String existingEmail = "test@email.com";
//
//        // when
//        userService.create(dto);
//
//        BusinessException exception = assertThrows(BusinessException.class,
//                () -> userService.emailDuplicateCheck(existingEmail));
//
//        // then
//        assertEquals(DUPLICATED_EMAIL, exception.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("카카오 로그인")
//    void kakaoLogin() throws JsonProcessingException {
//        //given
//        User user = new User();
//        user.setStatus("K");
//        user.setLoginType(LoginType.KAKAO);
//        user.setEmail("test@email.com");
//        user.setPhone("01023456789");
//
//        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@email.com", 1, "1234", LoginType.KAKAO, "USER");
//
//        //when
//        TokenInfo returnTokenInfo = userService.login(loginRequestDTO);
//
//        //then
//        assertThat(returnTokenInfo).isNotNull();
//        assertThat(returnTokenInfo.getGrantType()).isEqualTo("Bearer");
//        assertThat(returnTokenInfo.getAccessToken()).isNotEmpty();
//        assertThat(returnTokenInfo.getRefreshToken()).isNotEmpty();
//    }
}
