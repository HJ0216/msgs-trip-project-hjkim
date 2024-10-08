package com.msgs.domain.user.controller;

import static com.msgs.domain.user.exception.UserErrorCode.CHECK_LOGIN_ID_OR_PASSWORD;
import static com.msgs.domain.user.exception.UserErrorCode.DUPLICATED_EMAIL;
import static com.msgs.domain.user.exception.UserErrorCode.NOT_FOUND_MEMBER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msgs.domain.user.dto.LoginRequestDTO;
import com.msgs.domain.user.dto.SignUpRequestDTO;
import com.msgs.domain.user.service.UserService;
import com.msgs.global.common.error.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootTest
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class UserControllerTest {

  @Autowired
  MockMvc mockMvc;
  @Autowired
  ObjectMapper objectMapper;
  @MockBean
  UserService userService;

  @BeforeEach
  void setUp(WebApplicationContext webApplicationContext,
      RestDocumentationContextProvider restDocumentation) {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                                  .apply(documentationConfiguration(restDocumentation))
                                  .alwaysDo(print())
                                  .alwaysDo(document("users/{method-name}",
                                      preprocessRequest(prettyPrint()),
                                      preprocessResponse(prettyPrint())))
                                  .addFilters(new CharacterEncodingFilter("UTF-8", true))
                                  .build();
  }

  private void assertErrorResponse(ResultActions result, String code, String message)
      throws Exception {
    result.andExpect(jsonPath("$.code").value(code))
          .andExpect(jsonPath("$.message").value(message));
  }

  @Test
  @DisplayName("회원 가입: 성공")
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

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/new")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpDto)));
    // objectMapper.writeValueAsString(signUpDto)
    // DTO 객체를 JSON 문자열로 변환한 후, 이를 HTTP 요청으로 보냄
    // signUpDto의 값들이 변형되지 않지만, userService.create()가 호출될 때는 새로운 인스턴스가 생성

    // then
    result.andExpect(status().isCreated());
  }

  @Test
  @DisplayName("회원 가입: 실패 - 중복된 이메일")
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
    // when
    doThrow(new BusinessException(DUPLICATED_EMAIL))
        .when(userService).create(any(SignUpRequestDTO.class));

    ResultActions result = mockMvc.perform(post("/api/v2/users/new")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpDto)));

    // then
    result.andExpect(status().isBadRequest());
    assertErrorResponse(result, DUPLICATED_EMAIL.name(), DUPLICATED_EMAIL.getMessage());
  }

  @Test
  @DisplayName("회원 가입: 실패 - 잘못된 이메일 형식")
  void createFailInvalidEmail() throws Exception {
    // given
    SignUpRequestDTO signUpDto = SignUpRequestDTO.builder()
                                                 .userType("MSGS")
                                                 .email("email@email")
                                                 .phone("01023698745")
                                                 .nickname("name")
                                                 .password("temp123!")
                                                 .confirmPassword("temp123!")
                                                 .build();

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/new")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpDto)));

    System.out.println("result = " + result);

    // then
    result.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errors[0].field").value("email")) // email 필드의 에러 확인
          .andExpect(jsonPath("$.errors[0].message").value("이메일 형식이 올바르지 않습니다."))
          .andDo(print());// 에러 메시지 검증
  }

  @Test
  @DisplayName("로그인: 성공")
  void loginSuccess() throws Exception {
    // given
    LoginRequestDTO loginDto = LoginRequestDTO.builder()
                                              .email("temp@email.com")
                                              .password("temp123!")
                                              .build();

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginDto)));

    // then
    result.andExpect(status().isOk());
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

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginDto)));

    // then
    result.andExpect(status().isNotFound());
    assertErrorResponse(result, NOT_FOUND_MEMBER.name(), NOT_FOUND_MEMBER.getMessage());
  }

  @Test
  @DisplayName("로그인: 실패 - 잘못된 비밀번호")
  void loginFailWrongPassword() throws Exception {
    // given
    LoginRequestDTO loginDto = LoginRequestDTO.builder()
                                              .email("temp@email.com")
                                              .password("temp123_!")
                                              .build();

    when(userService.login(any(LoginRequestDTO.class)))
        .thenThrow(new BusinessException(CHECK_LOGIN_ID_OR_PASSWORD));

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginDto)));

    // then
    result.andExpect(status().isUnauthorized());
    assertErrorResponse(result, CHECK_LOGIN_ID_OR_PASSWORD.name(),
        CHECK_LOGIN_ID_OR_PASSWORD.getMessage());
  }

  @Test
  void findMyInfo() {
  }

  @Test
  void reissue() {
  }
}