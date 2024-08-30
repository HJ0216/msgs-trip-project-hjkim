package com.msgs.msgs.jwt;

import com.msgs.msgs.dto.TokenInfo;
import com.msgs.msgs.dto.UserPrinciple;
import com.msgs.msgs.error.BusinessException;
import com.msgs.msgs.redis.RedisUtil;
import com.msgs.user.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

import static com.msgs.msgs.error.ErrorCode.*;


@Component // Spring이 이 클래스를 자동으로 스캔하고 빈으로 등록
@RequiredArgsConstructor
public class JwtTokenProvider {
    private static final String AUTHORITIES_KEY  = "roles";
    private static final String BEARER_TYPE = "Bearer";

    @Value("${jwt.secretKey}")
    private String JWT_SECRET;

    private final RedisUtil redisUtil;
    private final UserRepository userRepository;

    // 유저 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
    public TokenInfo generateToken(UserPrinciple auth) {
        Date now = new Date();

        Date expiration = new Date(now.getTime() + Duration.ofMinutes(5).toMillis());

        String authorites = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Key secretKey = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(auth.getEmail())
                .claim(AUTHORITIES_KEY, authorites)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();

        // Refresh Token 생성
        // 아무런 정보도 토큰에 넣지 않고, 단순히 IssuedAt과 Expiration만을 입력한 후 서명
        String refreshToken = Jwts.builder()
                .setIssuedAt(now)
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
        //유저 정보 부분만 가져옴
        Claims claims = parseClaims(accessToken);
        if(claims == null)
            throw new BusinessException(NOT_FOUND_MEMBER);

        if(claims.getSubject() == null)
            throw new BusinessException(NOT_FOUND_MEMBER);

        if (claims.get(AUTHORITIES_KEY) == null)
            throw new BusinessException(NOT_FOUND_AUTHORITY);


        // 클레임에서 권한 정보 가져오기
        Set<GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY)
                        .toString()
                        .split(","))
                        .map(SecurityUtils::convertToAuthority) // 스트림의 각 요소를 다른 형태로 변환하는 데 사용
                        .collect(Collectors.toSet());


        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails userDetails = UserPrinciple.builder()
                .email(claims.getSubject())
                .authorities(authorities)
                .build();

        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }
    /**
     * UsernamePasswordAuthenticationToken
     * userDetails: 사용자의 정보(예: 이메일, 권한)를 담고 있는 객체
     * null: 이 위치에 보통은 비밀번호나 인증 토큰이 들어가지만, 여기서는 이미 인증이 된 상태이므로 필요하지 않아 null로 설정
     * authorities: 사용자가 가지고 있는 권한 목록
     * */

    // 토큰 정보를 검증하는 메서드
    public boolean isValidToken(String accessToken) {
        Key secretKey = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        Claims claims = parseClaims(accessToken);

        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(secretKey)
//                    .build()
//                    .parseClaimsJws(accessToken);
            //토큰에 claims 데이터가 없으면 사용불가 false 리턴
            if(claims == null)
                return false;
            //토큰사용기간 만료시에도 사용불가 false 리턴
            if (claims.getExpiration().before(new Date())) return false;

            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            System.out.println("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            System.out.println("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            System.out.println("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalStateException e) {
            System.out.println("JWT 토큰이 잘못되었습니다.");
        }

        return false;
    }

    // 리퀘스트의 토큰에서 암호풀어 Claims 가져오기
    private Claims parseClaims(String accessToken) {

        if(redisUtil.hasKeyBlackList(accessToken)){
            throw new BusinessException(LOGOUT_MEMBER);
        }

        if(accessToken == null)
            return null;

        Key secretKey = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();
    }

    public Long getExpiration(String token) {
        Key secretKey = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        // accessToken 남은 유효시간
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        // 현재 시간
        Long now = new Date().getTime();

        return (expiration.getTime() - now);
    }
}
