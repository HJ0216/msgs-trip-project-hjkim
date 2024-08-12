package com.msgs.msgs.jwt;

import com.msgs.msgs.dto.LoginRequestDTO;
import com.msgs.msgs.dto.TokenInfo;
import com.msgs.msgs.entity.user.User;
import com.msgs.msgs.error.BusinessException;
import com.msgs.msgs.error.ErrorCode;
import com.msgs.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.msgs.msgs.error.ErrorCode.NOT_FOUND_AUTHORITY;

@Slf4j
@Component
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY  = "auth";
    private static final String AUTHORITIES_VALUE_USER  = "USER";
    private static final String LOGIN_TYPE_KEY  = "auth";
    private static final String BEARER_TYPE = "Bearer";

    private final UserRepository userRepository;
    private final Key secretKey;

    public JwtTokenProvider(UserRepository userRepository, @Value("${jwt.secretKey}") String secretKey) {
        this.userRepository = userRepository;
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
    public TokenInfo generateToken(LoginRequestDTO loginRequestDTO) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + Duration.ofMinutes(1).toMillis());

        User user = userRepository.findById(loginRequestDTO.getId())
                .orElseThrow(
                        () -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(loginRequestDTO.getEmail())
                .claim(AUTHORITIES_KEY, AUTHORITIES_VALUE_USER)
                .claim(LOGIN_TYPE_KEY, loginRequestDTO.getLoginType())
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();

        return TokenInfo.builder()
                .grantType(BEARER_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new BusinessException(NOT_FOUND_AUTHORITY);
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY)
                        .toString()
                        .split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal =
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    // 토큰 정보를 검증하는 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다. ");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalStateException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }

        return false;
    }

    // 복호화 메서드
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public JSONObject getUserInfo(String accessToken) {
        Claims claims = parseClaims(accessToken);

        // 클레임에서 권한 정보 가져오기
        String userId = claims.get("jti").toString();
        String userEmail = claims.get("sub").toString();

        // user 정보 Json 에 담기
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", userId);
        jsonObject.put("email", userEmail);


        return jsonObject;
    }

}
