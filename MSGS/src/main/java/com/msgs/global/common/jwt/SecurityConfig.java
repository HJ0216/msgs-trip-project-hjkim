package com.msgs.global.common.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration // 스프링의 환경설정 파일임을 의미하는 애너테이션
@EnableWebSecurity // 모든 요청 URL이 스프링 시큐리티의 제어를 받도록 만드는 애너테이션
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    // 스프링 시큐리티의 인증을 담당
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 스프링 시큐리티의 세부 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> corsConfigurationSource())
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )                .authorizeRequests((auth) -> auth
                                .requestMatchers("/api/v2/users/login").permitAll()
                                .requestMatchers("/api/v2/users/me") .permitAll()
//                    .requestMatchers("/mypage/**").hasRole("USER")
//                    .requestMatchers("/admin/**").hasRole("ADMIN")
//                    .anyRequest().authenticated() // 이 외의 접근은 인증이 필요
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.sendRedirect("/"))
                );

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
     * addFilterBefore: JWT 인증 필터 추가
     * JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
     * */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 위에서 정의한 CORS 설정을 적용
        return source;
    }
}