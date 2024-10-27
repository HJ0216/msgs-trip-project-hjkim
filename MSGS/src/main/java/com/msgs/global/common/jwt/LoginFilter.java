package com.msgs.global.common.jwt;

import static com.msgs.domain.user.exception.UserErrorCode.INVALID_CREDENTIALS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msgs.domain.user.dto.request.LoginRequestDTO;
import com.msgs.global.common.error.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException {

    try {
      // JSON 요청 본문을 LoginRequestDto로 매핑
      LoginRequestDTO loginRequestDto = objectMapper.readValue(request.getInputStream(),
          LoginRequestDTO.class);

      String email = loginRequestDto.getEmail();
      String password = loginRequestDto.getPassword();

      log.info("username: {}, password: {}", email, password);

      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
          email, password, null);

      return authenticationManager.authenticate(authToken);

    } catch (IOException e) {
      log.error("Failed to parse authentication request body", e);
      throw new BusinessException(INVALID_CREDENTIALS);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authentication) {
    log.info("Success Login");
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException failed) {
    log.error("Login failed: ", failed);
  }

}
