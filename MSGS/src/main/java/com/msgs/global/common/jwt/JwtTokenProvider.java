package com.msgs.global.common.jwt;

import static com.msgs.global.common.error.CustomErrorCode.EXPIRED_JWT;
import static com.msgs.global.common.error.CustomErrorCode.ILLEGAL_STATE_JWT;
import static com.msgs.global.common.error.CustomErrorCode.MALFORMED_JWT;
import static com.msgs.global.common.error.CustomErrorCode.NOT_FOUND_AUTHORITY;
import static com.msgs.global.common.error.CustomErrorCode.NOT_FOUND_MEMBER;
import static com.msgs.global.common.error.CustomErrorCode.UNSUPPORTED_JWT;

import com.msgs.global.common.error.BusinessException;
import com.msgs.global.common.redis.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Component // Spring이 이 클래스를 자동으로 스캔하고 빈으로 등록
@RequiredArgsConstructor
public class JwtTokenProvider {

  private static final String AUTHORITIES_KEY = "roles";
  private static final String BEARER_TYPE = "Bearer";

  @Value("${jwt.secret}")
  private String jwtSecretKey;

  private final RedisUtil redisUtil;

  // 유저 정보를 가지고 AccessToken, RefreshToken을 생성하는 메서드
  public TokenInfo generateToken(UserDetails auth) {
    String accessToken = generateAccessToken(auth).getAccessToken();
    String refreshToken = generateRefreshToken(auth);

    return TokenInfo.builder()
                    .grantType(BEARER_TYPE)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
  }

  // 아무런 정보도 토큰에 넣지 않고, 단순히 IssuedAt과 Expiration만을 입력한 후 서명
  public String generateRefreshToken(UserDetails auth) {
    Key secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));

    Date now = new Date();
    Date expirationRefreshToken = new Date(now.getTime() + Duration.ofMinutes(2).toMillis());

    String refreshToken = Jwts.builder()
                              .setIssuedAt(now)
                              .setExpiration(expirationRefreshToken)
                              .signWith(secretKey)
                              .compact();

    Long expiration = expirationRefreshToken.getTime() - now.getTime();

    redisUtil.set("RT:" + refreshToken, auth.getUsername(), expiration);

    return refreshToken;
  }

  public TokenInfo generateAccessToken(UserDetails auth) {
    Key secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));

    Date now = new Date();
    Date expirationAccessToken = new Date(now.getTime() + Duration.ofMinutes(1).toMillis());

    String authorites = auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.joining(","));

    String accessToken = Jwts.builder()
                             .setSubject(auth.getUsername()) // 토큰 소유자 설정
                             .claim(AUTHORITIES_KEY, authorites) // JWT 내부에 추가적인 정보 설정
                             .setIssuedAt(now)
                             .setExpiration(expirationAccessToken)
                             .signWith(secretKey)
                             .compact();

    return TokenInfo.builder()
                    .grantType(BEARER_TYPE)
                    .accessToken(accessToken)
                    .build();

  }

  // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
  public Authentication getAuthentication(String accessToken) {
    //유저 정보 부분만 가져옴
    Claims claims = parseClaims(accessToken);
    if (claims == null) {
      throw new BusinessException(NOT_FOUND_MEMBER);
    }

    if (claims.get(AUTHORITIES_KEY) == null) {
      throw new BusinessException(NOT_FOUND_AUTHORITY);
    }

    // 클레임에서 권한 정보 가져오기
    Set<GrantedAuthority> authorities =
        Arrays.stream(claims.get(AUTHORITIES_KEY)
                            .toString()
                            .split(","))
              .map(SecurityUtils::convertToAuthority) // 스트림의 각 요소를 다른 형태로 변환
              .collect(Collectors.toSet());

    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
        claims.getSubject(),
        "",
        authorities
    );

    return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    // credentials: null, 인증이 완료된 후에는 실제 자격 증명(예: 비밀번호)을 사용할 필요가 없으므로 null로 설정
  }

  /**
   * UsernamePasswordAuthenticationToken userDetails: 사용자의 정보(예: 이메일, 권한)를 담고 있는 객체 null: 이 위치에 보통은
   * 비밀번호나 인증 토큰이 들어가지만, 여기서는 이미 인증이 된 상태이므로 필요하지 않아 null로 설정 authorities: 사용자가 가지고 있는 권한 목록
   */

  // 토큰 정보를 검증하는 메서드
  public boolean isValidAccessToken(String accessToken) {
    if (redisUtil.hasKeyBlackList("AT:" + accessToken)) {
      return false;
    }

    try {
      parseClaims(accessToken);

      return true;
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

  private Claims parseClaims(String token) {
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
