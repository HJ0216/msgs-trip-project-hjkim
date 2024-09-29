package com.msgs.main.service;

import static com.msgs.domain.user.exception.UserErrorCode.DUPLICATED_EMAIL;
import static com.msgs.domain.user.exception.UserErrorCode.NOT_FOUND_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.msgs.domain.user.domain.User;
import com.msgs.domain.user.dto.SignUpRequestDTO;
import com.msgs.domain.user.repository.UserRepository;
import com.msgs.domain.user.service.UserService;
import com.msgs.global.common.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class UserServiceTest {

  @Autowired
  UserService userService;
  @Autowired
  UserRepository userRepository;

  @Test
  @DisplayName("회원 가입")
  void userSignUp() throws Exception {
    // given
    SignUpRequestDTO dto = SignUpRequestDTO.builder()
                                           .userType("MSGS")
                                           .email("test0907@email.com")
                                           .phone("01075395468")
                                           .nickname("hello")
                                           .password("1234")
                                           .build();

    // when
    userService.create(dto);

    // then
    User savedUser = userRepository.findByEmail(dto.getEmail()).orElseThrow(
        () -> new BusinessException(NOT_FOUND_MEMBER));

    // 필드 값 비교
    assertThat(savedUser.getEmail()).isEqualTo(dto.getEmail());
    assertThat(savedUser.getPhone()).isEqualTo(dto.getPhone());
  }

  @Test
  @DisplayName("이메일이 동일한 회원이 존재할 경우, 예외가 발생한다.")
  void emailDuplicateCheck() {
    // given
    SignUpRequestDTO dto = SignUpRequestDTO.builder()
                                           .userType("MSGS")
                                           .email("test0907@email.com")
                                           .phone("01013579513")
                                           .nickname("hello")
                                           .password("1234")
                                           .build();

    String existingEmail = "test0907@email.com";

    // when
    userService.create(dto);

    BusinessException exception = assertThrows(BusinessException.class,
        () -> userService.emailDuplicateCheck(existingEmail));

    // then
    assertEquals(DUPLICATED_EMAIL, exception.getErrorCode());
  }
}
