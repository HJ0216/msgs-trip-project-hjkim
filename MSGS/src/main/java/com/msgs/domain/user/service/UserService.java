package com.msgs.domain.user.service;

import com.msgs.domain.user.dto.SignUpRequestDTO;
import lombok.RequiredArgsConstructor;

import com.msgs.domain.user.dto.LoginRequestDTO;
import com.msgs.domain.user.dto.LogoutRequestDTO;
import com.msgs.domain.user.dto.UserDTO;
import com.msgs.global.common.jwt.UserPrinciple;
import com.msgs.domain.user.repository.UserRepository;
import com.msgs.global.common.jwt.TokenInfo;
import com.msgs.domain.user.domain.User;
import com.msgs.global.common.error.BusinessException;
import com.msgs.global.common.jwt.JwtTokenProvider;
import com.msgs.global.common.jwt.SecurityUtils;
import com.msgs.global.common.redis.RedisUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

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

    public void emailDuplicateCheck(String email){
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

        //스프링 시큐리티에서 로그인하기
        authenticationManagerBuilder
                .getObject()
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        // User의 role -> 스프링시큐리티의 GrantedAuthority로 변경
        // 여러개의 role을 가질수 있으므로 Set
        Set<GrantedAuthority> authorities = Set.of(SecurityUtils.convertToAuthority(user.getRole()));

        UserPrinciple userPrinciple = UserPrinciple.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .user(user)
                .build();

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(userPrinciple);

        storeRefreshToken(user.getEmail(), tokenInfo.getRefreshToken());

        return tokenInfo;
    }

    private void storeRefreshToken(String email, String refreshToken){
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        // opsForValue: Strings를 쉽게 Serialize / Deserialize 해주는 interface
        Long expiration = jwtTokenProvider.getExpiration(refreshToken);
        valueOperations.set("RT:" + email, refreshToken, expiration, TimeUnit.MILLISECONDS);
    }

    public UserDTO findMyInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));

        return UserDTO.toUserDTO(user);
    }

    public void logout(LogoutRequestDTO logoutRequestDto) {
        // 1. Access Token 검증
        if (!jwtTokenProvider.isValidToken(logoutRequestDto.getAccessToken())) {
            throw new BusinessException(INVALID_ACCESS_TOKEN);
        }

        // 2. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(logoutRequestDto.getAccessToken());

        // 3. Redis 에서 해당 User email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            // Refresh Token 삭제
            redisTemplate.delete("RT:" + authentication.getName());
        }

        // 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtTokenProvider.getExpiration(logoutRequestDto.getAccessToken());
        redisTemplate.opsForValue()
                .set(logoutRequestDto.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);
    }

    public TokenInfo reissue(LogoutRequestDTO reIssueDto) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.isValidToken(reIssueDto.getRefreshToken())) {
            throw new BusinessException(INVALID_REFRESH_TOKEN);
        }

        // 2. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(reIssueDto.getAccessToken());

        // 3. Redis에서 User email을 기반으로 저장된 Refresh Token 값을 가져옵니다.
        String refreshToken = redisTemplate.opsForValue().get("RT:" + authentication.getName());
        // (추가) 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
        if(ObjectUtils.isEmpty(refreshToken)) {
            throw new BusinessException(LOGOUT_MEMBER);
        }

        if(!refreshToken.equals(reIssueDto.getRefreshToken())) {
            throw new BusinessException(INVALID_REFRESH_TOKEN);
        }

        // 4. 새로운 토큰 생성
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));

        Set<GrantedAuthority> authorities = Set.of(SecurityUtils.convertToAuthority(user.getRole()));

        UserPrinciple userPrinciple = UserPrinciple.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .user(user)
                .build();

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(userPrinciple);

        // 5. RefreshToken Redis 업데이트
        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName()
                        , tokenInfo.getRefreshToken()
                        , 5, TimeUnit.MINUTES);

        return tokenInfo;
    }
}
