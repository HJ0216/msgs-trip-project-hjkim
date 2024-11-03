package com.msgs.global.common.jwt;

import static com.msgs.domain.user.exception.UserErrorCode.INVALID_CREDENTIALS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msgs.domain.user.dto.request.LoginRequestDTO;
import com.msgs.global.common.error.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

/*    UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal(); // 현재 인증된 사용자에 대한 주요 정보를 반환

    String username = userPrinciple.getUsername();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority authority = iterator.next();
    String role = authority.getAuthority();

    String token = jwtUtils.generateJwt(username, role, 60 * 60 * 60 * 1L);
    response.addHeader("Authorization", "Bearer " + token);*/

    String username = authentication.getName(); // email

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority authority = iterator.next();
    String role = authority.getAuthority();

    String accessToken = jwtUtils.generateJwt("access", username, role, 600000L);
    String refreshToken = jwtUtils.generateJwt("refresh", username, role, 86400000L);

    response.setHeader("Authorization", "Bearer " + accessToken);
    response.addCookie(generateCookie("refresh", refreshToken));
    response.setStatus(HttpStatus.OK.value());
  }

  private Cookie generateCookie(String key, String value) {
    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(24 * 60 * 60); // 1시간
//    cookie.setSecure(true); // 쿠키가 HTTPS 연결을 통해서만 전송되도록 설정
//    cookie.setPath("/"); // 쿠키가 사용할 수 있는 URL 경로를 지정
    cookie.setHttpOnly(true);
    // 쿠키를 JavaScript와 같은 클라이언트 측, 스크립트에서 접근할 수 없도록 설정
    // JavaScript의 document.cookie를 사용해 쿠키의 값을 읽거나 수정할 수 없음
    // XSS(크로스 사이트 스크립팅) 공격으로부터 쿠키를 보호하는 데 도움

    return cookie;
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException failed) {
    log.error("Login failed: ", failed);

    response.setStatus(401);
  }

}
