package com.msgs.global.common.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JWTUtils {

  private Key key;

  public JWTUtils(@Value("${jwt.secret}") String secret) {
    // decode: Base64로 인코딩된 문자열을 원래의 바이트 배열로 복원
    byte[] byteSecretKey = Decoders.BASE64.decode(secret);
    key = Keys.hmacShaKeyFor(byteSecretKey);

    // getBytes: 문자열을 특정 문자 인코딩(예: UTF-8)에 따라 바이트 배열로 변환
    // 문자열을 다른 데이터 형식(바이트 배열)으로 단순히 바꾸는 것
//    key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

  }

  public String getCategory(String token) {
    return Jwts.parserBuilder()
               .setSigningKey(key)
               .build()
               .parseClaimsJws(token)
               .getBody()
               .get("category", String.class);

    // parseClaimsJws: JWS (JSON Web Signature) 형식의 토큰을 파싱
    // 제공된 비밀 키를 사용하여 토큰의 유효성을 확인
    // parseClaimsJwt: JWT 형식의 토큰을 파싱하지만, 서명 검증을 수행하지 않음
  }

  public String getUsername(String token) {
    return Jwts.parserBuilder()
               .setSigningKey(key)
               .build()
               .parseClaimsJws(token)
               .getBody()
               .get("username", String.class);
  }

  public String getRole(String token) {
    return Jwts.parserBuilder()
               .setSigningKey(key)
               .build()
               .parseClaimsJws(token)
               .getBody()
               .get("role", String.class);
  }

  public Boolean isExpired(String token) {
    return Jwts.parserBuilder()
               .setSigningKey(key)
               .build()
               .parseClaimsJws(token)
               .getBody()
               .getExpiration()
               .before(new Date());
  }

  public String generateJwt(String category, String username, String role, Long expiredMs) {
    Claims claims = Jwts.claims();
    claims.put("category", category);
    claims.put("username", username);
    claims.put("role", role);

    return Jwts.builder()
               .setClaims(claims)
               .setIssuedAt(new Date(System.currentTimeMillis()))
               .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
               .signWith(key, SignatureAlgorithm.HS256)
               .compact();
  }
}
