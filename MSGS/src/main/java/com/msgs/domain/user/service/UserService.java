package com.msgs.domain.user.service;

import com.msgs.domain.user.dto.SignUpRequestDTO;
import com.msgs.global.common.jwt.SecurityUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;

import com.msgs.domain.user.dto.LoginRequestDTO;
import com.msgs.domain.user.dto.LogoutRequestDTO;
import com.msgs.domain.user.dto.UserDTO;
import com.msgs.domain.user.repository.UserRepository;
import com.msgs.global.common.jwt.TokenInfo;
import com.msgs.domain.user.domain.User;
import com.msgs.global.common.error.BusinessException;
import com.msgs.global.common.jwt.JwtTokenProvider;
import com.msgs.global.common.redis.RedisUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.msgs.global.common.error.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public void create(SignUpRequestDTO dto){
        emailDuplicateCheck(dto.getEmail());
        userRepository.save(dto.toEntity());
    }

    private void emailDuplicateCheck(String email){
        if(!userRepository.findByEmail(email).isEmpty()){
            throw new BusinessException(DUPLICATED_EMAIL);
        }
    }

    public TokenInfo login(LoginRequestDTO loginRequestDTO){
        User user = userRepository.findByEmail(loginRequestDTO.getEmail()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));

        // 비밀번호 일치 여부 비교
        if(!loginRequestDTO.getPassword().equals(user.getPassword())) {
            throw new BusinessException(NOT_EQUAL_PASSWORD);
        }

        // SecurityContextHolder에 저장
        // authenticate(): 전달된 인증 객체(Authentication)를 사용하여 인증되지 않은 사용자 인증을 수행
        // 내부적으로 설정된 AuthenticationProvider들을 순차적으로 사용하여 자격 증명을 검증
        // AuthenticationProvider: UserDetailsService 호출
        // UserDetailsService: 데이터베이스에서 해당 이메일을 가진 사용자를 찾으면, 그 사용자 정보(UserDetails)를 반환
        // * 해당 이메일이 존재하지 않는다면, UsernameNotFoundException가 발생하여 인증 실패
        Authentication authentication = authenticationManagerBuilder
                .getObject()
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(userDetails);

        // 로그인 시, Refresh Token 정보를 Redis에 저장
        // Refresh Token 정보를 기반으로 Access Token 정보 재 발행
        storeRefreshToken(tokenInfo.getRefreshToken(), user.getEmail());

        return tokenInfo;
    }

    private void storeRefreshToken(String refreshToken, String email){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        // opsForValue: Strings를 쉽게 Serialize / Deserialize 해주는 interface
        // 자바 언어에서 사용되는 Object 또는 Data를 다른 컴퓨터의 자바 시스템에서도 사용 할수 있도록 바이트 스트림(stream of bytes) 형태로 연속전인(serial) 데이터로 변환하는 포맷 변환 기술
        Long expiration = jwtTokenProvider.getExpiration(refreshToken);
        valueOperations.set("RT:" + refreshToken, email, expiration, TimeUnit.MILLISECONDS);
    }

    public UserDTO findMyInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));

        return UserDTO.toUserDTO(user);
    }

    public TokenInfo reissue(TokenInfo reIssueDto) {
        try {
            jwtTokenProvider.getExpiration(reIssueDto.getAccessToken());
            throw new BusinessException(VALID_ACCESS_TOKEN);
        } catch (ExpiredJwtException e) {
            // Refresh Token 유효성 검사
            if (!jwtTokenProvider.isValidToken(reIssueDto.getRefreshToken())) {
                throw new BusinessException(INVALID_REFRESH_TOKEN);
            }

            // Redis에 Refresh Token 존재 확인
            boolean hasStoredRefreshToken = redisTemplate.hasKey("RT:" + reIssueDto.getRefreshToken());
            if(!hasStoredRefreshToken) {
                throw new BusinessException(LOGOUT_MEMBER);
            }

            String email = redisTemplate.opsForValue().get("RT:" + reIssueDto.getRefreshToken());
            User user = userRepository.findByEmail(email).orElseThrow(
                    () -> new BusinessException(NOT_FOUND_MEMBER));

            // AccessToken 재발급

            // User의 role -> 스프링시큐리티의 GrantedAuthority로 변경
            // 여러개의 role을 가질수 있으므로 Set
            UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    Set.of(SecurityUtils.convertToAuthority(user.getRole()))
            );

            return jwtTokenProvider.generateAccessToken(userDetails);
        }
    }

    public void logout(LogoutRequestDTO logoutRequestDto) {
        // 1. Access Token 검증
        if (!jwtTokenProvider.isValidToken(logoutRequestDto.getAccessToken())) {
            throw new BusinessException(INVALID_ACCESS_TOKEN);
        }

        // 2. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(logoutRequestDto.getAccessToken());

        // 3. Redis 에서 해당 User email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        if (redisTemplate.opsForValue().get(logoutRequestDto.getAccessToken()) != null) {
            // Refresh Token 삭제
            redisTemplate.delete("RT:" + authentication.getName());
        }

        // 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtTokenProvider.getExpiration(logoutRequestDto.getAccessToken());
        redisTemplate.opsForValue()
                .set("AT:" + logoutRequestDto.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);
    }
}
