package com.msgs.global.common.jwt;

import com.msgs.global.common.error.BusinessException;
import com.msgs.global.common.redis.RedisUtil;
import com.msgs.domain.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.msgs.global.common.error.ErrorCode.*;


@Component // Spring이 이 클래스를 자동으로 스캔하고 빈으로 등록
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY  = "roles";
    private static final String BEARER_TYPE = "Bearer";

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    // 유저 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public TokenInfo generateToken(UserDetails auth) {
        Date now = new Date();

        Date expirationAccessToken = new Date(now.getTime() + Duration.ofMinutes(1).toMillis());
        Date expirationRefreshToken = new Date(now.getTime() + Duration.ofMinutes(2).toMillis());

        String authorites = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Key secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(auth.getUsername()) // 토큰 소유자 설정
                .claim(AUTHORITIES_KEY, authorites) // JWT 내부에 추가적인 정보 설정
                .setIssuedAt(now)
                .setExpiration(expirationAccessToken)
                .signWith(secretKey)
                .compact();

        // Refresh Token 생성
        // 아무런 정보도 토큰에 넣지 않고, 단순히 IssuedAt과 Expiration만을 입력한 후 서명
        String refreshToken = Jwts.builder()
                .setIssuedAt(now)
                .setExpiration(expirationRefreshToken)
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
        //유저 정보 부분만 가져옴
        Claims claims = parseClaims(accessToken);
        if(claims == null)
            throw new BusinessException(NOT_FOUND_MEMBER);

        if (claims.get(AUTHORITIES_KEY) == null)
            throw new BusinessException(NOT_FOUND_AUTHORITY);


        // 클레임에서 권한 정보 가져오기
        Set<GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY)
                        .toString()
                        .split(","))
                        .map(SecurityUtils::convertToAuthority) // 스트림의 각 요소를 다른 형태로 변환
                        .collect(Collectors.toSet());


        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails userDetails = UserPrinciple.builder()
                .email(claims.getSubject())
                .authorities(authorities)
                .build();

        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        // credentials: null, 인증이 완료된 후에는 실제 자격 증명(예: 비밀번호)을 사용할 필요가 없으므로 null로 설정
    }
    /**
     * UsernamePasswordAuthenticationToken
     * userDetails: 사용자의 정보(예: 이메일, 권한)를 담고 있는 객체
     * null: 이 위치에 보통은 비밀번호나 인증 토큰이 들어가지만, 여기서는 이미 인증이 된 상태이므로 필요하지 않아 null로 설정
     * authorities: 사용자가 가지고 있는 권한 목록
     * */

    // 토큰 정보를 검증하는 메서드
    public boolean isValidToken(String token) {
        if(token == null || token.isEmpty())
            return false;

        try {
            return (!parseClaims(token).isEmpty());
        } catch (MalformedJwtException e) {
            throw new BusinessException(MALFORMED_JWT);
        } catch (ExpiredJwtException e) {
            throw new BusinessException(EXPIRED_JWT);
        } catch (UnsupportedJwtException e) {
            throw new BusinessException(UNSUPPORTED_JWT);
        } catch (IllegalStateException e) {
            throw new BusinessException(ILLEGAL_STATE_JWT);
        }
    }

    // 리퀘스트의 토큰에서 암호풀어 Claims 가져오기
    private Claims parseClaims(String token) {
        if(redisUtil.hasKeyBlackList(token)){
            throw new BusinessException(LOGOUT_MEMBER);
        }

        Key secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getExpiration(String token) {
        Key secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));

        // Token 남은 유효시간
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        // 현재 시간
        Long now = new Date().getTime();

        return (expiration.getTime() - now); // TimeUnit.MILLISECONDS
    }
}
