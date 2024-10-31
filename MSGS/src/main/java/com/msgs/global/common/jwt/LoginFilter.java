package com.msgs.global.common.jwt;

import static com.msgs.domain.user.exception.UserErrorCode.INVALID_CREDENTIALS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msgs.domain.user.dto.request.LoginRequestDTO;
import com.msgs.global.common.error.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

  private final AuthenticationManager authenticationManager;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private final JWTUtils jwtUtils;

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

    UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal(); // 현재 인증된 사용자에 대한 주요 정보를 반환

    String username = userPrinciple.getUsername();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority authority = iterator.next();
    String role = authority.getAuthority();

    String token = jwtUtils.generateJwt(username, role, 60 * 60 * 60 * 1L);
    response.addHeader("Authorization", "Bearer " + token);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException failed) {
    log.error("Login failed: ", failed);

    response.setStatus(401);
  }

}
