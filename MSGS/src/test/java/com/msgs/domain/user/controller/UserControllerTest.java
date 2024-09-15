package com.msgs.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msgs.domain.user.dto.SignUpRequestDTO;
import com.msgs.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean UserService userService;

    @Test
    @DisplayName("Controller: 회원 가입")
    void create() throws Exception {
        // given
        SignUpRequestDTO signUpDto = SignUpRequestDTO.builder()
                .status("M")
                .email("Tcontroller@email.com")
                .phone("01023698741")
                .nickname("Tname")
                .password("test123!")
                .confirmPassword("test123!")
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
    void login() {
    }

    @Test
    void findMyInfo() {
    }

    @Test
    void reissue() {
    }
}