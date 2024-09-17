package com.msgs.domain.user.service;

import io.jsonwebtoken.ExpiredJwtException;

import lombok.RequiredArgsConstructor;

import com.msgs.domain.user.domain.User;
import com.msgs.domain.user.dto.LoginRequestDTO;
import com.msgs.domain.user.dto.SignUpRequestDTO;
import com.msgs.domain.user.dto.UserDTO;
import com.msgs.domain.user.repository.UserRepository;
import com.msgs.global.common.error.BusinessException;
import com.msgs.global.common.jwt.SecurityUtils;
import com.msgs.global.common.jwt.TokenInfo;
import com.msgs.global.common.jwt.JwtTokenProvider;
import com.msgs.global.common.redis.RedisUtil;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.msgs.global.common.error.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;

    @Transactional
    public void create(SignUpRequestDTO signUpRequestDTO){
        emailDuplicateCheck(signUpRequestDTO.getEmail());
        userRepository.save(signUpRequestDTO.toEntity());
    }

    public void emailDuplicateCheck(String email){
        if(userRepository.findByEmail(email).isPresent()){
            throw new BusinessException(DUPLICATED_EMAIL);
        }
    }

    public TokenInfo login(LoginRequestDTO loginRequestDTO){
        User user = userRepository.findByEmail(loginRequestDTO.getEmail()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));

        // 비밀번호 일치 여부 비교
        // TODO: 240915, passwordEncoder 적용
        if(!loginRequestDTO.getPassword().equals(user.getPassword())) {
            throw new BusinessException(PASSWORD_CONFIRM_VALIDATION);
        }

        // SecurityContextHolder에 저장
        // authenticate(): 전달된 인증 객체(Authentication)를 사용하여 인증되지 않은 사용자 인증을 수행
        // 내부적으로 설정된 AuthenticationProvider들을 순차적으로 사용하여 자격 증명을 검증
        // AuthenticationProvider: UserDetailsService 호출
        // UserDetailsService: 데이터베이스에서 해당 이메일을 가진 사용자를 찾으면, 그 사용자 정보(UserDetails)를 반환
        // * 해당 이메일이 존재하지 않는다면, UsernameNotFoundException이 발생하여 인증 실패
        Authentication authentication = authenticationManagerBuilder
                .getObject()
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        TokenInfo tokenInfo = jwtTokenProvider.generateToken(userDetails);

        return tokenInfo;
    }

    public UserDTO findMyInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));

        return UserDTO.toUserDTO(user);
    }

    public TokenInfo reissue(TokenInfo reissueRequestDto) {
        try {
            jwtTokenProvider.getExpiration(reissueRequestDto.getAccessToken());
            throw new BusinessException(VALID_ACCESS_TOKEN);
        } catch (ExpiredJwtException e) {
            // Redis에 Refresh Token 존재 확인
            boolean hasStoredRefreshToken = redisUtil.hasKey("RT:" + reissueRequestDto.getRefreshToken());
            if(!hasStoredRefreshToken) {
                throw new BusinessException(LOGOUT_MEMBER);
            }

            String email = (String) redisUtil.get("RT:" + reissueRequestDto.getRefreshToken());
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

    public void logout(TokenInfo logoutRequestDto) {
        if (!jwtTokenProvider.isValidAccessToken(logoutRequestDto.getAccessToken())) {
            throw new BusinessException(INVALID_ACCESS_TOKEN);
        }

        if (redisUtil.get("RT:" + logoutRequestDto.getRefreshToken()) != null) {
            redisUtil.delete("RT:" + logoutRequestDto.getRefreshToken());
        }

        Long expiration = jwtTokenProvider.getExpiration(logoutRequestDto.getAccessToken());
        redisUtil.setBlackList("AT:" + logoutRequestDto.getAccessToken(), "logout", expiration);
    }
}
