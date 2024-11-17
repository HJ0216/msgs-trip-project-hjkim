package com.msgs.global.common.jwt;

import static com.msgs.domain.user.exception.UserErrorCode.EXPIRED_JWT;
import static com.msgs.domain.user.exception.UserErrorCode.MALFORMED_JWT;
import static com.msgs.domain.user.exception.UserErrorCode.REFRESH_TOKEN_IS_NULL;

import com.msgs.global.common.error.BusinessException;
import com.msgs.global.common.redis.RedisUtils;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

  private static final String REFRESH_TOKEN_KEY = "refresh";

  private final JWTUtils jwtUtils;
  private final RedisUtils redisUtils;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
  }

  private void doFilter(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws IOException, ServletException {

    // logout 경로인지 확인
    String requestUri = request.getRequestURI();
    if (!requestUri.matches("^\\/api\\/v2\\/users\\/logout$")) {
      filterChain.doFilter(request, response);

      return;
    }

    String requestMethod = request.getMethod();
    if (!requestMethod.equals("POST")) {
      filterChain.doFilter(request, response);

      return;
    }

    String refreshToken = null;

    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(REFRESH_TOKEN_KEY)) {
        refreshToken = cookie.getValue();
      }
    }

    if (refreshToken == null) {
//      response.setStatus(REFRESH_TOKEN_IS_NULL.getHttpStatus().value());
      throw new BusinessException(REFRESH_TOKEN_IS_NULL);
    }

    try {
      jwtUtils.isExpired(refreshToken);
    } catch (ExpiredJwtException e) {
//      response.setStatus(EXPIRED_JWT.getHttpStatus().value());
      throw new BusinessException(EXPIRED_JWT);
    }

    String category = jwtUtils.getCategory(refreshToken);

    if (!category.startsWith(REFRESH_TOKEN_KEY)) {
//      response.setStatus(MALFORMED_JWT.getHttpStatus().value());
      throw new BusinessException(MALFORMED_JWT);
    }

    // Redis에 RT 저장 확인
    boolean isExistRefreshToken = redisUtils.hasKey("RT:" + refreshToken);

    if (!isExistRefreshToken) {
//      response.setStatus(REFRESH_TOKEN_IS_NULL.getHttpStatus().value());
      throw new BusinessException(REFRESH_TOKEN_IS_NULL);
    }

    // 로그아웃
    redisUtils.delete("RT:" + refreshToken);

    Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, null);
    cookie.setMaxAge(0);
    cookie.setHttpOnly(true);
    response.addCookie(cookie);

    // 현재 요청을 보낸 사용자의 인증 정보 초기화
    SecurityContextHolder.clearContext();

    response.setStatus(HttpServletResponse.SC_OK);
  }
}
