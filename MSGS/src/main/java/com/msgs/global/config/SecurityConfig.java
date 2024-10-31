package com.msgs.global.config;

import com.msgs.global.common.jwt.JWTFilter;
import com.msgs.global.common.jwt.JWTUtils;
import com.msgs.global.common.jwt.JwtAuthenticationFilter;
import com.msgs.global.common.jwt.JwtTokenProvider;
import com.msgs.global.common.jwt.LoginFilter;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration // 스프링의 환경설정 파일임을 의미하는 애너테이션
@EnableWebSecurity // 모든 요청 URL이 스프링 시큐리티의 제어를 받도록 만드는 애너테이션
@RequiredArgsConstructor
public class SecurityConfig {

  private final AuthenticationConfiguration authenticationConfiguration;
  private final JwtTokenProvider jwtTokenProvider;
  private final JWTUtils jwtUtils;


  // 스프링 시큐리티의 인증을 담당
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  // 스프링 시큐리티의 세부 설정
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    // 기본 HTTP 인증 비활성화
    http.httpBasic(AbstractHttpConfigurer::disable);

    // CSRF 보호 비활성화: CSRF 토큰 대신 JWT 같은 토큰을 사용하여 요청 검증
    // CSRF 보호를 비활성화, CSRF 토큰을 사용하여 클라이언트가 서버에 요청할 때마다 유효한 토큰을 함께 전송해야만 요청이 성공하도록 하는 방식으로 보안을 강화 -> 토큰으로 대체
    http.csrf(AbstractHttpConfigurer::disable);

    // CORS 설정 적용
    http.cors(httpSecurityCorsConfigurer ->
        corsConfigurationSource()
    );

    // 세션 관리 설정: 상태 저장하지 않는 무상태(stateless) 설정
    http.sessionManagement(sessionManagement ->
        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    );

    http.authorizeHttpRequests(auth ->
        auth
            .requestMatchers("/api/v2/users/new", "/api/v2/users/login")
            .permitAll() // 회원가입과 로그인은 인증 없이 접근 가능
            .requestMatchers("/api/v2/users/me", "/api/v2/users/nickname",
                "/api/v2/users/password", "/api/v2/users/logout",
                "/api/v2/users/reissue").hasRole("USER") // 특정 엔드포인트에 대해 USER 역할이 필요
            .anyRequest().permitAll() // 나머지 요청은 모두 접근 허용
    );

    // 필터 체인에 예외 처리 필터 추가: UsernamePasswordAuthenticationFilter 이전에 추가
//    http.addFilterBefore(new ExceptionHandlerFilter(), UsernamePasswordAuthenticationFilter.class);

    // JWT 인증 필터 추가: 예외 처리 필터 이후에 추가
//    http.addFilterAfter(new JwtAuthenticationFilter(jwtTokenProvider),
//        ExceptionHandlerFilter.class);

    //
    http.addFilterBefore(new JWTFilter(jwtUtils), LoginFilter.class);

    // 사용자 로그인 필터 추가: UsernamePasswordAuthenticationFilter 위치에 추가
    // 사용자 로그인 필터 추가: /api/v2/users/login 경로에 맞춰 설정
    LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration),
        jwtUtils);
    loginFilter.setFilterProcessesUrl("/api/v2/users/login");
    http.addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

    // 구성된 필터 체인 빌드
    return http.build();
  }
  /**
   * CSRF(Cross Site Request Forgery): 사이트간 위조 요청
   * csrf(): HTTP 요청에 대해 CSRF 토큰을 생성하고, 이 토큰을 쿠키에 저장
   * 모든 POST, PUT, DELETE 요청에 대해 CSRF 토큰이 함께 전송되는지 검증
   *
   * REST API를 이용한 서버라면, session 기반 인증과는 다르게 stateless(= 서버에 인증 정보를 보관하지 않음)
   * 사용자가 로그인 상태를 유지하고 있는 동안 다른 사이트에서 악의적인 요청을 보내는 것을 방지하는 데 유용한 CSRF 사용 필요성이 낮음
   * 대신, Client는 권한이 필요한 요청을 하기 위해서는 요청에 필요한 인증 정보를(OAuth2, jwt토큰 등)을 포함시켜야 함
   *
   * AbstractHttpConfigurer::disable
   * AbstractHttpConfigurer 클래스 안에 disable 메서드를 참조
   * */

  /**
   * CORS(Cross-Origin Resource Sharing): 웹 페이지가 자신과 다른 출처의 리소스에 접근할 때 발생하는 보안 정책
   * httpSecurityCorsConfigurer를 corsConfigurationSource()로 정의
   * */

  /**
   * authorizeRequests: 특정 URL 경로에 대한 접근 권한을 설정
   * requestMatchers("...").permitAll()은 "..." 엔드포인트에 대한 모든 요청을 허용
   * 즉, 인증되지 않은 사용자도 이 경로에 접근할 수 있음
   * */

  /**
   * addFilterBefore: JWT 인증 필터 추가 JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에
   * 추가
   */

  // 특정 경로에 대해 모든 보안 필터 제외
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring()
//                .requestMatchers("/api/v2/users/new", "/api/v2/users/reissue");
//    }

  // 특정 경로에 대해 특정 보안 필터 제외
  public JwtAuthenticationFilter jwtAuthenticationFilterForSpecificUrls() {
    return new JwtAuthenticationFilter(jwtTokenProvider) {
      @Override
      protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return !("/api/v2/users/login".equals(path) || "/api/v2/users/me".equals(path)
            || "/api/v2/users/nickname".equals(path) || "/api/v2/users/password".equals(path)
            || "/api/v2/users/reissue".equals(path) || "/api/v2/users/logout".equals(path));
      }
    };
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("*"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(List.of("*"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 위에서 정의한 CORS 설정을 적용
    return source;
  }
}