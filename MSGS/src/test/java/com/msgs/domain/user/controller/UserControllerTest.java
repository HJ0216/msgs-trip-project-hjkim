package com.msgs.domain.user.controller;

import static com.msgs.domain.user.exception.UserErrorCode.CHECK_LOGIN_ID_OR_PASSWORD;
import static com.msgs.domain.user.exception.UserErrorCode.DUPLICATED_EMAIL;
import static com.msgs.domain.user.exception.UserErrorCode.EXPIRED_JWT;
import static com.msgs.domain.user.exception.UserErrorCode.INVALID_ACCESS_TOKEN;
import static com.msgs.domain.user.exception.UserErrorCode.INVALID_REFRESH_TOKEN;
import static com.msgs.domain.user.exception.UserErrorCode.LOGOUT_MEMBER;
import static com.msgs.domain.user.exception.UserErrorCode.NOT_FOUND_MEMBER;
import static com.msgs.domain.user.exception.UserErrorCode.VALID_ACCESS_TOKEN;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msgs.domain.user.domain.UserType;
import com.msgs.domain.user.dto.UserDTO;
import com.msgs.domain.user.dto.request.LoginRequestDTO;
import com.msgs.domain.user.dto.request.SignUpRequestDTO;
import com.msgs.domain.user.dto.request.UpdateUserNicknameRequestDTO;
import com.msgs.domain.user.dto.request.UpdateUserPasswordRequestDTO;
import com.msgs.domain.user.service.UserService;
import com.msgs.global.common.error.BusinessException;
import com.msgs.global.common.jwt.TokenInfo;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootTest
//@MockBean(UserController.class)
//@MockBean(JpaMetamodelMappingContext.class)
@ExtendWith({RestDocumentationExtension.class})
// RestDocumentationExtension: Spring REST Docs의 기능을 테스트에 확장하여 API 문서화를 자동으로 할 수 있게 함
// RestDocumentationContextProvider 객체를 제공하여 MockMvc 설정 시 REST Docs 문서화를 할 수 있게 도와줌
// 테스트 실행 시 REST Docs 관련 설정을 수동으로 추가할 수 있도록 도와줌
@AutoConfigureMockMvc
// @AutoConfigureRestDocs
// Spring REST Docs를 자동 설정해 문서화를 지원
// Spring Boot에서 자동으로 REST Docs 설정을 구성하여 MockMvc 객체에 REST Docs 관련 설정을 추가
// MockMvc 객체 생성 시 REST Docs 설정을 자동으로 추가
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
    this.mockMvc = MockMvcBuilders.webAppContextSetup(
                                      webApplicationContext) // webApplicationContext 기반 Mock 객체 생성
                                  .apply(documentationConfiguration(
                                      restDocumentation)) // REST Docs의 문서화를 위한 설정을 추가
                                  .alwaysDo(print())
                                  .alwaysDo(document("users/{method-name}",
                                      preprocessRequest(prettyPrint()),
                                      preprocessResponse(prettyPrint())))
                                  // REST Docs 문서화 작업을 자동으로 실행
                                  // {method-name}을 통해 각 테스트 메서드 이름에 따라 문서화 파일을 생성
                                  .addFilters(new CharacterEncodingFilter("UTF-8", true))
                                  .build();
  }

  private void assertErrorResponse(ResultActions result, String code, String message)
      throws Exception {
    result.andExpect(jsonPath("$.code").value(code))
          .andExpect(jsonPath("$.message").value(message))
          .andDo(print());
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

    // then
    result.andExpect(status().isBadRequest())
          .andExpect(
              jsonPath("$.errors[?(@.field == 'email')].message").value("이메일 형식이 올바르지 않습니다."))
          .andDo(print());// 에러 메시지 검증
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
    result.andExpect(status().isConflict());
    assertErrorResponse(result, DUPLICATED_EMAIL.name(), DUPLICATED_EMAIL.getMessage());
  }

  @Test
  @DisplayName("회원 가입: 실패 - 잘못된 비밀번호 형식")
  void createFailInvalidPassword() throws Exception {
    // given
    SignUpRequestDTO signUpDto = SignUpRequestDTO.builder()
                                                 .userType("MSGS")
                                                 .email("email@email.com")
                                                 .phone("01023698745")
                                                 .nickname("name")
                                                 .password("1234")
                                                 .confirmPassword("1234")
                                                 .build();

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/new")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpDto)));

    // then
    result.andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.errors[?(@.field == 'password')].message").value(
              "비밀번호는 8~20자의 영문자, 특수문자, 숫자를 포함해야 합니다."))// annotation에서 나오는 error message 반환
          .andDo(print());// 에러 메시지 검증
  }

  @Test
  @DisplayName("회원 가입: 실패 - 잘못된 이메일과 비밀번호 형식")
  void createFailInvalidEmailAndPassword() throws Exception {
    // given
    SignUpRequestDTO signUpDto = SignUpRequestDTO.builder()
                                                 .userType("MSGS")
                                                 .email("email@email")
                                                 .phone("01023698745")
                                                 .nickname("name")
                                                 .password("1234")
                                                 .confirmPassword("1234")
                                                 .build();

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/new")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpDto)));

    // then
    // 에러 메시지 발생 순서가 보장되지 않음 -> field 탐색 방식으로 jsonPath 변경
    result.andExpect(status().isBadRequest())
          .andExpect(
              jsonPath("$.errors[?(@.field == 'email')].message").value("이메일 형식이 올바르지 않습니다."))
          .andExpect(
              jsonPath("$.errors[?(@.field == 'password')].message").value(
                  "비밀번호는 8~20자의 영문자, 특수문자, 숫자를 포함해야 합니다."))
          .andDo(print());
  }

  @Test
  @DisplayName("회원 가입: 실패 - 비밀번호 확인 불일치")
  void createFailInvalidPasswordConfirm() throws Exception {
    // given
    SignUpRequestDTO signUpDto = SignUpRequestDTO.builder()
                                                 .userType("MSGS")
                                                 .email("email@email.com")
                                                 .phone("01023698745")
                                                 .nickname("name")
                                                 .password("temp123!")
                                                 .confirmPassword("1234")
                                                 .build();

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/new")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpDto)));

    // then
    result.andExpect(status().isBadRequest())
          .andExpect(
              jsonPath("$.errors[?(@.field == 'validPasswordConfirmed')].message").value(
                  "비밀번호와 비밀번호 확인이 일치하지 않습니다."))
          // 검증 실패 시 반환되는 에러 메시지는 해당 메서드 이름을 기반으로 자동 생성된 필드 이름(passwordConfirmed)을 사용
          .andDo(print());
  }

  @Test
  @DisplayName("회원 가입: 실패 - 잘못된 전화번호 형식")
  void createFailInvalidPhoneNumber() throws Exception {
    // given
    SignUpRequestDTO signUpDto = SignUpRequestDTO.builder()
                                                 .userType("MSGS")
                                                 .email("email@email.com")
                                                 .phone("010-2369-8745")
                                                 .nickname("name")
                                                 .password("temp123!")
                                                 .confirmPassword("temp123!")
                                                 .build();

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/new")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpDto)));

    // then
    result.andExpect(status().isBadRequest())
          .andExpect(
              jsonPath("$.errors[?(@.field == 'phone')].message").value(
                  "전화번호 형식이 올바르지 않습니다."))
          .andDo(print());
  }

  @Test
  @DisplayName("회원 가입: 실패 - 잘못된 닉네임 형식")
  void createFailInvalidNickname() throws Exception {
    // given
    SignUpRequestDTO signUpDto = SignUpRequestDTO.builder()
                                                 .userType("MSGS")
                                                 .email("email@email.com")
                                                 .phone("01023698745")
                                                 .nickname("too-long-nickname")
                                                 .password("temp123!")
                                                 .confirmPassword("temp123!")
                                                 .build();

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/new")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpDto)));

    // then
    result.andExpect(status().isBadRequest())
          .andExpect(
              jsonPath("$.errors[?(@.field == 'nickname')].message").value(
                  "닉네임은 2~8자의 한글, 영문 대/소문자, 숫자로만 이뤄져야 합니다."))
          .andDo(print());
  }

  @Test
  @DisplayName("회원 가입: 실패 - 잘못된 회원 타입")
  void createFailInvalidUserType() throws Exception {
    // given
    SignUpRequestDTO signUpDto = SignUpRequestDTO.builder()
                                                 .userType("NEVER")
                                                 .email("email@email.com")
                                                 .phone("01023698745")
                                                 .nickname("nickname")
                                                 .password("temp123!")
                                                 .confirmPassword("temp123!")
                                                 .build();

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/new")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpDto)));

    // then
    result.andExpect(status().isBadRequest())
          .andExpect(
              jsonPath("$.errors[?(@.field == 'validUserType')].message").value(
                  "회원 타입이 올바르지 않습니다."))
          .andDo(print());
  }

  @Test
  @DisplayName("로그인: 성공")
  void loginSuccess() throws Exception {
    // given
    LoginRequestDTO loginDto = LoginRequestDTO.builder()
                                              .email("temp@email.com")
                                              .password("temp123!")
                                              .build();

    TokenInfo expectedResult = TokenInfo.builder()
                                        .grantType("Bearer")
                                        .accessToken("accessToken")
                                        .refreshToken("refreshToken")
                                        .build();

    given(userService.login(any())).willReturn(expectedResult);

//    given(userService.login(argThat(dto ->
//        dto.getEmail().equals(loginDto.getEmail()) &&
//            dto.getPassword().equals(loginDto.getPassword())
//    ))).willReturn(expectedResult);

    ArgumentCaptor<LoginRequestDTO> captor = ArgumentCaptor.forClass(LoginRequestDTO.class);

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginDto)));

    verify(userService).login(captor.capture());
    LoginRequestDTO capturedDto = captor.getValue();

    System.out.println("Original loginDto instance address: " + System.identityHashCode(loginDto));
    System.out.println(
        "Captured loginDto instance address: " + System.identityHashCode(capturedDto));

    // then
    result.andExpect(status().isOk())
          .andExpect(jsonPath("$.accessToken").value("accessToken"))
          .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
  }

  @Test
  @DisplayName("로그인 실패 - 가입하지 않은 회원")
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
  @DisplayName("로그인: 실패 - 이메일 및 비밀번호 입력 X")
  void loginFailEmptyEmailAndPassword() throws Exception {
    // given
    LoginRequestDTO loginDto = LoginRequestDTO.builder()
                                              .email("")
                                              .password("")
                                              .build();

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginDto)));

    // then
    result.andExpect(status().isBadRequest())
          .andExpect(
              jsonPath("$.errors[?(@.field == 'email')].message").value(
                  "이메일은 필수값입니다."))
          .andExpect(
              jsonPath("$.errors[?(@.field == 'password')].message").value(
                  "비밀번호는 필수값입니다."))
          .andDo(print());
  }

  @Test
  @DisplayName("정보 조회: 성공")
  void findMyInfoSuccess() throws Exception {
    // given
    LocalDateTime createdDate = LocalDateTime.now().minusDays(1).truncatedTo(ChronoUnit.MILLIS);
    LocalDateTime updatedDate = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
    UserDTO userDto = UserDTO.builder()
                             .id(300)
                             .status("M")
                             .userType(UserType.MSGS)
                             .role("ROLE_USER")
                             .email("msgs@msgs.com")
                             .phone("010-2024-1017")
                             .nickname("MSGS")
                             .password("password123!")
                             .imagePath("")
                             .createdDate(createdDate)
                             .updatedDate(updatedDate)
                             .build();

    given(userService.findMyInfo()).willReturn(userDto);

    // when
    ResultActions result = mockMvc.perform(get("/api/v2/users/me")
        .contentType(MediaType.APPLICATION_JSON));

    verify(userService).findMyInfo();

    // then
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    result.andExpect(status().isOk())
          .andExpect(jsonPath("$.id").value(300))
          .andExpect(jsonPath("$.status").value("M"))
          .andExpect(jsonPath("$.userType").value("MSGS"))
          .andExpect(jsonPath("$.role").value("ROLE_USER"))
          .andExpect(jsonPath("$.email").value("msgs@msgs.com"))
          .andExpect(jsonPath("$.phone").value("010-2024-1017"))
          .andExpect(jsonPath("$.nickname").value("MSGS"))
          .andExpect(jsonPath("$.imagePath").value(""))
          .andExpect(jsonPath("$.createdDate").value(createdDate.format(formatter)))
          .andExpect(jsonPath("$.updatedDate").value(updatedDate.format(formatter)));
  }

  @Test
  @DisplayName("정보 조회: 실패 - 만료된 Access Token")
  void findMyInfoFailExpiredToken() throws Exception {
    // given
    String expiredToken = "expiredAccessToken";

    when(userService.findMyInfo())
        .thenThrow(new BusinessException(EXPIRED_JWT));

    // when
    ResultActions result = mockMvc.perform(get("/api/v2/users/me")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + expiredToken));

    // then
    result.andExpect(status().isUnauthorized());
    assertErrorResponse(result, EXPIRED_JWT.name(),
        EXPIRED_JWT.getMessage());
  }

  @Test
  @DisplayName("정보 조회: 실패 - 유효하지 않은 Access Token")
  void findMyInfoFailInvalidToken() throws Exception {
    // given
    String invalidToken = "invalidAccessToken";

    when(userService.findMyInfo())
        .thenThrow(new BusinessException(INVALID_ACCESS_TOKEN));

    // when
    ResultActions result = mockMvc.perform(get("/api/v2/users/me")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidToken));

    // then
    result.andExpect(status().isUnauthorized());
    assertErrorResponse(result, INVALID_ACCESS_TOKEN.name(),
        INVALID_ACCESS_TOKEN.getMessage());
  }

  @Test
  @DisplayName("정보 조회: 실패 - 존재하지 않는 회원")
  void findMyInfoFailNotFoundMember() throws Exception {
    // given
    String accessToken = "accessToken";

    when(userService.findMyInfo())
        .thenThrow(new BusinessException(NOT_FOUND_MEMBER));

    // when
    ResultActions result = mockMvc.perform(get("/api/v2/users/me")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken));

    // then
    result.andExpect(status().isNotFound());
    assertErrorResponse(result, NOT_FOUND_MEMBER.name(),
        NOT_FOUND_MEMBER.getMessage());
  }

  @Test
  @DisplayName("토큰 재발급: 성공")
  void reissueSuccess() throws Exception {
    // given
    TokenInfo previousTokenInfo = TokenInfo.builder()
                                           .grantType("Bearer")
                                           .accessToken("previousAT")
                                           .refreshToken("previouseRT")
                                           .build();

    TokenInfo newTokenInfo = TokenInfo.builder()
                                      .grantType("Bearer")
                                      .accessToken("newAT")
                                      .refreshToken("newRT")
                                      .build();

    given(userService.reissue(any(TokenInfo.class))).willReturn(newTokenInfo);

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/reissue")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(previousTokenInfo)));

    // then
    result.andExpect(status().isOk())
          .andExpect(jsonPath("$.accessToken").value("newAT"))
          .andExpect(jsonPath("$.refreshToken").value("newRT"));
  }

  @Test
  @DisplayName("토큰 재발급: 실패 - 유효한 Access Token")
  void reissueFailValidAccessToken() throws Exception {
    // given
    TokenInfo validTokenInfo = TokenInfo.builder()
                                        .grantType("Bearer")
                                        .accessToken("validAT")
                                        .refreshToken("validRT")
                                        .build();

    when(userService.reissue(any(TokenInfo.class)))
        .thenThrow(new BusinessException(VALID_ACCESS_TOKEN));

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/reissue")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(validTokenInfo)));

    // then
    result.andExpect(status().isOk());
    assertErrorResponse(result, VALID_ACCESS_TOKEN.name(),
        VALID_ACCESS_TOKEN.getMessage());
  }

  @Test
  @DisplayName("토큰 재발급: 실패 - 로그아웃한 회원")
  void reissueFailLogout() throws Exception {
    // given
    TokenInfo logoutTokenInfo = TokenInfo.builder()
                                         .grantType("Bearer")
                                         .accessToken("logoutAT")
                                         .refreshToken("logoutRT")
                                         .build();

    when(userService.reissue(any(TokenInfo.class)))
        .thenThrow(new BusinessException(LOGOUT_MEMBER));

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/reissue")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(logoutTokenInfo)));

    // then
    result.andExpect(status().isUnauthorized());
    assertErrorResponse(result, LOGOUT_MEMBER.name(),
        LOGOUT_MEMBER.getMessage());
  }

  @Test
  @DisplayName("토큰 재발급: 실패 - 유효하지 않은 Refresh Token")
  void reissueFailInvalidRefreshToken() throws Exception {
    // given
    TokenInfo invalidTokenInfo = TokenInfo.builder()
                                          .grantType("Bearer")
                                          .accessToken("invalidAT")
                                          .refreshToken("invalidRT")
                                          .build();

    when(userService.reissue(any(TokenInfo.class)))
        .thenThrow(new BusinessException(INVALID_REFRESH_TOKEN));

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/reissue")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidTokenInfo)));

    // then
    result.andExpect(status().isUnauthorized());
    assertErrorResponse(result, INVALID_REFRESH_TOKEN.name(),
        INVALID_REFRESH_TOKEN.getMessage());
  }

  @Test
  @DisplayName("토큰 재발급: 실패 - 존재하지 않는 회원")
  void reissueFailNotFoundMember() throws Exception {
    // given
    TokenInfo invalidTokenInfo = TokenInfo.builder()
                                          .grantType("Bearer")
                                          .accessToken("invalidAT")
                                          .refreshToken("invalidRT")
                                          .build();

    when(userService.reissue(any(TokenInfo.class)))
        .thenThrow(new BusinessException(NOT_FOUND_MEMBER));

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/reissue")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidTokenInfo)));

    // then
    result.andExpect(status().isNotFound());
    assertErrorResponse(result, NOT_FOUND_MEMBER.name(),
        NOT_FOUND_MEMBER.getMessage());
  }

  @Test
  @DisplayName("로그아웃: 성공")
  void logoutSuccess() throws Exception {
    // given
    TokenInfo logoutTokenInfo = TokenInfo.builder()
                                         .grantType("Bearer")
                                         .accessToken("logoutAT")
                                         .refreshToken("logoutRT")
                                         .build();

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/logout")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(logoutTokenInfo)));

    // then
    result.andExpect(status().isOk());
  }

  @Test
  @DisplayName("로그아웃: 실패 - 유효하지 않은 Access Token")
  void logoutFailInvalidAccessToken() throws Exception {
    // given
    TokenInfo invalidTokenInfo = TokenInfo.builder()
                                          .grantType("Bearer")
                                          .accessToken("invalidAT")
                                          .refreshToken("invalidRT")
                                          .build();

    doThrow(new BusinessException(INVALID_ACCESS_TOKEN))
        .when(userService).logout(any(TokenInfo.class));

    // when
    ResultActions result = mockMvc.perform(post("/api/v2/users/logout")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(invalidTokenInfo)));

    // then
    result.andExpect(status().isUnauthorized());
    assertErrorResponse(result, INVALID_ACCESS_TOKEN.name(),
        INVALID_ACCESS_TOKEN.getMessage());
  }

  @Test
  @DisplayName("닉네임 수정: 성공")
  void updateNicknameSuccess() throws Exception {
    // given
    UpdateUserNicknameRequestDTO request = UpdateUserNicknameRequestDTO.builder()
                                                                       .nickname("newNick")
                                                                       .build();

    // when
    ResultActions result = mockMvc.perform(patch("/api/v2/users/nickname")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isOk());
  }

  @Test
  @DisplayName("닉네임 수정: 실패 - 잘못된 닉네임 형식")
  void updateNicknameFailNickname() throws Exception {
    // given
    UpdateUserNicknameRequestDTO request = UpdateUserNicknameRequestDTO.builder()
                                                                       .nickname("nick-name")
                                                                       .build();

    // when
    ResultActions result = mockMvc.perform(patch("/api/v2/users/nickname")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isBadRequest())
          .andExpect(
              jsonPath("$.errors[?(@.field == 'nickname')].message").value(
                  "닉네임은 2~8자의 한글, 영문 대/소문자, 숫자로만 이뤄져야 합니다."))
          .andDo(print());

  }

  @Test
  @DisplayName("닉네임 수정: 실패 - 존재하지 않는 회원")
  void updateNicknameFailNotFoundMember() throws Exception {
    // given
    UpdateUserNicknameRequestDTO request = UpdateUserNicknameRequestDTO.builder()
                                                                       .nickname("nickname")
                                                                       .build();

    doThrow(new BusinessException(NOT_FOUND_MEMBER))
        .when(userService).updateNickname(any(String.class));

    // when
    ResultActions result = mockMvc.perform(patch("/api/v2/users/nickname")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isNotFound());
    assertErrorResponse(result, NOT_FOUND_MEMBER.name(),
        NOT_FOUND_MEMBER.getMessage());
  }

  @Test
  @DisplayName("비밀번호 수정: 성공")
  void updatePasswordSuccess() throws Exception {
    // given
    UpdateUserPasswordRequestDTO request = UpdateUserPasswordRequestDTO.builder()
                                                                       .password("newPass123!")
                                                                       .confirmPassword(
                                                                           "newPass123!")
                                                                       .build();

    // when
    ResultActions result = mockMvc.perform(patch("/api/v2/users/password")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    // then
    result.andExpect(status().isOk());
  }

}