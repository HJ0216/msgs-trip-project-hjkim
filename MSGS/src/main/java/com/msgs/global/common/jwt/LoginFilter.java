package com.msgs.global.common.jwt;

import static com.msgs.domain.user.exception.UserErrorCode.CHECK_LOGIN_ID_OR_PASSWORD;
import static com.msgs.domain.user.exception.UserErrorCode.INVALID_CREDENTIALS;
import static com.msgs.global.common.error.CommonErrorCode.REDIS_CONNECTION_ERROR;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msgs.domain.user.dto.request.LoginRequestDTO;
import com.msgs.global.common.error.BusinessException;
import com.msgs.global.common.redis.RedisUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpHeaders;
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

  private static final long ACCESS_TOKEN_EXPIRY = 60000L; // 2분
  private static final long REFRESH_TOKEN_EXPIRY = 600000L; // 10분

  private final AuthenticationManager authenticationManager;
  private final JWTUtils jwtUtils;
  private final RedisUtils redisUtils;

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

    String username = authentication.getName(); // email

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority authority = iterator.next();
    String role = authority.getAuthority();

    String accessToken = jwtUtils.generateJwt("access", username, role, ACCESS_TOKEN_EXPIRY);
    String refreshToken = jwtUtils.generateJwt("refresh", username, role, REFRESH_TOKEN_EXPIRY);

    // refresh token 저장
    try {
      redisUtils.set("RT:" + refreshToken, username, REFRESH_TOKEN_EXPIRY);
    } catch (RedisConnectionFailureException e) {
      log.error("Redis connection failed", e);
      throw new BusinessException(REDIS_CONNECTION_ERROR);
    }

    response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
    response.addCookie(generateCookie("refresh", refreshToken));
    response.setStatus(HttpStatus.OK.value());
  }

  private Cookie generateCookie(String key, String value) {
    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge((int) REFRESH_TOKEN_EXPIRY / 1000); // 1시간
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
    log.error("Login failed: ", failed.getClass());

    throw new BusinessException(CHECK_LOGIN_ID_OR_PASSWORD);
  }

}
