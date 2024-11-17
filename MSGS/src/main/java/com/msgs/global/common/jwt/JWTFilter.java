package com.msgs.global.common.jwt;

import static com.msgs.domain.user.exception.UserErrorCode.EXPIRED_JWT;
import static com.msgs.domain.user.exception.UserErrorCode.INVALID_ACCESS_TOKEN;

import com.msgs.domain.user.domain.User;
import com.msgs.global.common.error.BusinessException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

  private final JWTUtils jwtUtils;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

/*
    // request에서 Authorization 헤더를 찾음
    String authorization = request.getHeader("Authorization");

    if (authorization == null || !authorization.startsWith("Bearer ")) {
      log.warn("Token is null");

      filterChain.doFilter(request, response); // req, res를 다음 필터로 넘겨줌

      // 조건에 해당할 경우, 메서드 필수 종료
      return;
    }

    String token = authorization.split(" ")[1];

    String username = jwtUtils.getUsername(token);
    String role = jwtUtils.getRole(token);

    User findUser = User.builder()
                        .email(username)
                        .role(role)
                        .build();

    UserPrinciple userPrinciple = UserPrinciple.builder()
                                               .user(findUser)
                                               .build();

    Authentication authToken = new UsernamePasswordAuthenticationToken(userPrinciple, null,
        userPrinciple.getAuthorities());
    // Authentication: Spring Security에서 인증된 사용자의 정보와 권한을 담는 인터페이스
    // UsernamePasswordAuthenticationToken: Authentication 구현체
    // UserPrinciple: 사용자 정보를 담고 있는 객체
    // Credentials: 인증에 필요한 자격 증명 정보(보통 비밀번호). 인증 후에는 보안 때문에 보통 null로 설정
    // Authorities: 사용자가 가진 권한 목록 (예: ROLE_USER, ROLE_ADMIN)
    SecurityContextHolder.getContext().setAuthentication(authToken);
    // SecurityContext에 인증된 사용자 정보를 저장
    // 이후 요청 처리 과정에서 현재 요청을 보낸 사용자가 누구인지, 어떤 권한을 가지고 있는지를 참조할 수 있게 함

    filterChain.doFilter(request, response);
*/

    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

    // re-issue 요청인 경우, Access Token 유효성 검사를 건너뜀
    if (request.getRequestURI().equals("/api/v2/users/re-issue")) {
      filterChain.doFilter(request, response);
      return;
    }

    // 토큰이 없을 경우, 다음 필터로 넘김
    if (authorization == null || !authorization.startsWith("Bearer ")) {
      log.warn("Access Token is null");

      filterChain.doFilter(request, response);

      return;
    }

    String accessToken = authorization.split(" ")[1];

    try {
      jwtUtils.isExpired(accessToken);

    } catch (ExpiredJwtException e) {
      // 다음 필터로 넘기지 않고 바로 응답코드를 발생시킴
//      setResponse(response, EXPIRED_JWT);
      throw new BusinessException(EXPIRED_JWT);
    }

    // Access Token 확인
    String category = jwtUtils.getCategory(accessToken);

    if (!category.equals("access")) {
//      setResponse(response, INVALID_ACCESS_TOKEN);
      throw new BusinessException(INVALID_ACCESS_TOKEN);
    }

    // 세션 생성
    String username = jwtUtils.getUsername(accessToken);
    String role = jwtUtils.getRole(accessToken);

    User findUser = User.builder()
                        .email(username)
                        .role(role)
                        .build();

    UserPrinciple userPrinciple = UserPrinciple.builder()
                                               .user(findUser)
                                               .build();

    Authentication authToken = new UsernamePasswordAuthenticationToken(userPrinciple, null,
        userPrinciple.getAuthorities());
    SecurityContextHolder.getContext().setAuthentication(authToken);

    filterChain.doFilter(request, response);
  }
}
