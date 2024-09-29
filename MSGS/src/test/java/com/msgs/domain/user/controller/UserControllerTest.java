package com.msgs.domain.user.controller;

import static com.msgs.domain.user.exception.UserErrorCode.CHECK_LOGIN_ID_OR_PASSWORD;
import static com.msgs.domain.user.exception.UserErrorCode.DUPLICATED_EMAIL;
import static com.msgs.domain.user.exception.UserErrorCode.NOT_FOUND_MEMBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msgs.domain.user.dto.LoginRequestDTO;
import com.msgs.domain.user.dto.SignUpRequestDTO;
import com.msgs.domain.user.service.UserService;
import com.msgs.global.common.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;
  @MockBean
  UserService userService;

  @Test
  @DisplayName("Controller: 회원 가입 성공")
  void createSuccess() throws Exception {
    // given
    SignUpRequestDTO signUpDto = SignUpRequestDTO.builder()
                                                 .userType("MSGS")
                                                 .email("new@email.com")
                                                 .phone("01023698741")
                                                 .nickname("name")
                                                 .password("newnew123!")
                                                 .confirmPassword("newnew123!")
                                                 .build();

    // when // then
    mockMvc.perform(post("/api/v2/users/new")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(signUpDto)))
           .andExpect(status().isCreated());

    // 회원 생성 메소드가 호출되었는지 확인
    verify(userService).create(refEq(signUpDto));
  }

  @Test
  @DisplayName("Controller: 회원 가입 실패, 중복된 이메일")
  void createFailDuplicateEmail() throws Exception {
    // given
    SignUpRequestDTO signUpDto = SignUpRequestDTO.builder()
                                                 .userType("MSGS")
                                                 .email("temp@email.com")
                                                 .phone("01023698745")
                                                 .nickname("name")
                                                 .password("temp123!")
                                                 .confirmPassword("temp123!")
                                                 .build();

    // when // then
    doThrow(new BusinessException(DUPLICATED_EMAIL))
        .when(userService).create(any(SignUpRequestDTO.class));

    mockMvc.perform(post("/api/v2/users/new")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(signUpDto)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.code").value("DUPLICATED_EMAIL"))
           .andExpect(jsonPath("$.message").value("이미 존재하는 이메일 입니다."));

    // 회원 생성 메소드가 호출되었는지 확인
    verify(userService).create(refEq(signUpDto));
  }

  @Test
  @DisplayName("Controller: 회원 가입 실패, 잘못된 이메일 형식")
  void createFailInvalidEmail() throws Exception {
    // given
    SignUpRequestDTO signUpRequestDTO = SignUpRequestDTO.builder()
                                                        .userType("MSGS")
                                                        .email("email@email")
                                                        .phone("01023698745")
                                                        .nickname("name")
                                                        .password("temp123!")
                                                        .confirmPassword("temp123!")
                                                        .build();

    // when // then
    mockMvc.perform(post("/api/v2/users/new")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(signUpRequestDTO)))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.errors[0].field").value("email")) // email 필드의 에러 확인
           .andExpect(
               jsonPath("$.errors[0].message").value("이메일 형식이 올바르지 않습니다.")); // 에러 메시지 검증

    // 회원 생성 메소드가 호출되었는지 확인
    verify(userService, never()).create(any(SignUpRequestDTO.class));
  }

  @Test
  @DisplayName("Controller: 로그인 성공")
  void loginSuccess() throws Exception {
    // given
    LoginRequestDTO loginDto = LoginRequestDTO.builder()
                                              .email("temp@email.com")
                                              .password("temp123!")
                                              .build();

    // when // then
    mockMvc.perform(post("/api/v2/users/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(loginDto)))
           .andExpect(status().isOk());

    verify(userService).login(refEq(loginDto));
  }

  @Test
  @DisplayName("Controller: 로그인 실패 - 가입하지 않은 회원")
  void loginFailUnjoinUser() throws Exception {
    // given
    LoginRequestDTO loginDto = LoginRequestDTO.builder()
                                              .email("tem_p@email.com")
                                              .password("temp123!")
                                              .build();

    when(userService.login(any(LoginRequestDTO.class)))
        .thenThrow(new BusinessException(NOT_FOUND_MEMBER));

    // when // then
    mockMvc.perform(post("/api/v2/users/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(loginDto)))
           .andExpect(status().isNotFound())
           .andExpect(jsonPath("$.code").value("NOT_FOUND_MEMBER"))
           .andExpect(jsonPath("$.message").value("존재하지 않는 회원입니다."));

    verify(userService).login(refEq(loginDto));
  }

  @Test
  @DisplayName("Controller: 로그인 실패 - 잘못된 비밀번호")
  void loginFailWrongPassword() throws Exception {
    // given
    LoginRequestDTO loginDto = LoginRequestDTO.builder()
                                              .email("temp@email.com")
                                              .password("temp123_!")
                                              .build();

    when(userService.login(any(LoginRequestDTO.class)))
        .thenThrow(new BusinessException(CHECK_LOGIN_ID_OR_PASSWORD));

    // when // then
    mockMvc.perform(post("/api/v2/users/login")
               .contentType(MediaType.APPLICATION_JSON)
               .content(objectMapper.writeValueAsString(loginDto)))
           .andExpect(status().isUnauthorized())
           .andExpect(jsonPath("$.code").value("CHECK_LOGIN_ID_OR_PASSWORD"))
           .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호를 확인해주세요."));

    verify(userService).login(refEq(loginDto));
  }

  @Test
  void findMyInfo() {
  }

  @Test
  void reissue() {
  }
}