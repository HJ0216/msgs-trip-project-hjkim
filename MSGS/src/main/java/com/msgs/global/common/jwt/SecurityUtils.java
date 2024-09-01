package com.msgs.global.common.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

public class SecurityUtils {
    public static final String ROLE_PREFIX = "ROLE_";

    //Token 값을 가져오는데 필요한 값들
    public static final String AUTH_HEADER = "Authorization";
    public static final String AUTH_TOKEN_TYPE = "Bearer";
    public static final String AUTH_TOKEN_PREFIX = AUTH_TOKEN_TYPE + " "; // 한 칸 띄우는것을 넣어줘야함

    //시큐리티에서 "ROLE_ADMIN" ,"ROLE_USER"로 설정하기 때문에 앞에 "ROLE"을 붙여줌
    public static SimpleGrantedAuthority convertToAuthority(String role) {
        String formattedRole = role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role;
        return new SimpleGrantedAuthority(formattedRole);
    }

    // Request Header에서 토큰 정보 추출
    public static String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTH_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AUTH_TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }

        return null;
    }

}
