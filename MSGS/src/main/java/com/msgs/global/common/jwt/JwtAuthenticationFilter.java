package com.msgs.global.common.jwt;

import com.msgs.global.common.error.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.msgs.global.common.error.ErrorCode.LOGOUT_MEMBER;
import static com.msgs.global.common.error.ErrorCode.NOT_EQUAL_PASSWORD;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    // 요청이 들어올 때마다 JWT 토큰을 검사하고 인증 정보를 설정
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    // JWT 토큰의 인증 정보를 현재 쓰레드의 SecurityContext에 저장하는 역할 수행
    @Override
    public void doFilterInternal(HttpServletRequest request
            , HttpServletResponse response
            , FilterChain filterChain
    ) throws IOException, ServletException {
        // 1. Request Header에서 JWT 토큰 추출
        String accessToken = SecurityUtils.resolveToken(request);

        // 2. 토큰 유효성 검사
        // 토큰이 유효할 경우, 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
        if (accessToken != null) {
            if(jwtTokenProvider.isValidAccessToken(accessToken)){
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new BusinessException(LOGOUT_MEMBER);
            }
        }

        filterChain.doFilter(request, response);
        // 현재 필터가 요청을 처리한 후, 필터 체인의 다음 필터로 요청과 응답을 전달
    }
}
