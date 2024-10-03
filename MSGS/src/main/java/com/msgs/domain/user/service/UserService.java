package com.msgs.domain.user.service;

import static com.msgs.domain.user.exception.UserErrorCode.DUPLICATED_EMAIL;
import static com.msgs.domain.user.exception.UserErrorCode.INVALID_ACCESS_TOKEN;
import static com.msgs.domain.user.exception.UserErrorCode.LOGOUT_MEMBER;
import static com.msgs.domain.user.exception.UserErrorCode.NOT_FOUND_MEMBER;
import static com.msgs.domain.user.exception.UserErrorCode.PASSWORD_CONFIRM_VALIDATION;
import static com.msgs.domain.user.exception.UserErrorCode.VALID_ACCESS_TOKEN;

import com.msgs.domain.user.domain.User;
import com.msgs.domain.user.dto.LoginRequestDTO;
import com.msgs.domain.user.dto.SignUpRequestDTO;
import com.msgs.domain.user.dto.UserDTO;
import com.msgs.domain.user.repository.UserRepository;
import com.msgs.global.common.error.BusinessException;
import com.msgs.global.common.jwt.JwtTokenProvider;
import com.msgs.global.common.jwt.SecurityUtils;
import com.msgs.global.common.jwt.TokenInfo;
import com.msgs.global.common.redis.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final UserRepository userRepository;
  private final JwtTokenProvider jwtTokenProvider;
  private final RedisUtil redisUtil;

  @Transactional
  public void create(SignUpRequestDTO signUpRequestDTO) {
    emailDuplicateCheck(signUpRequestDTO.getEmail());
    userRepository.save(signUpRequestDTO.toEntity());

    log.info("User successfully created for email: {}", signUpRequestDTO.getEmail());
  }

  public void emailDuplicateCheck(String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      log.warn("Email duplication check failed. Email: {}", email);
      throw new BusinessException(DUPLICATED_EMAIL);
    }
  }

  public TokenInfo login(LoginRequestDTO loginRequestDTO) {
    User user = userRepository.findByEmail(loginRequestDTO.getEmail()).orElseThrow(
        () -> {
          log.warn("User not found for email: {}", loginRequestDTO.getEmail());
          throw new BusinessException(NOT_FOUND_MEMBER);
        });

    // 비밀번호 일치 여부 비교
    // TODO: 240915, passwordEncoder 적용
    if (!loginRequestDTO.getPassword().equals(user.getPassword())) {
      log.warn("Password validation failed for user: {}", loginRequestDTO.getEmail());
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

    log.info("Generating token for user: {}", user.getEmail());

    return tokenInfo;
  }

  public UserDTO findMyInfo() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
        () -> {
          log.warn("User not found for email: {}", authentication.getName());
          throw new BusinessException(NOT_FOUND_MEMBER);
        });

    return UserDTO.toUserDTO(user);
  }

  public TokenInfo reissue(TokenInfo reissueRequestDto) {
    try {
      jwtTokenProvider.getExpiration(reissueRequestDto.getAccessToken());

      log.warn("Valid access token used for reissue: {}", reissueRequestDto.getAccessToken());
      throw new BusinessException(VALID_ACCESS_TOKEN);
    } catch (ExpiredJwtException e) {
      // Redis에 Refresh Token 존재 확인
      boolean hasStoredRefreshToken = redisUtil.hasKey("RT:" + reissueRequestDto.getRefreshToken());
      if (!hasStoredRefreshToken) {
        log.warn("Refresh token not found or expired in Redis. RefreshToken: {}",
            reissueRequestDto.getRefreshToken());
        throw new BusinessException(LOGOUT_MEMBER);
      }

      String email = (String) redisUtil.get("RT:" + reissueRequestDto.getRefreshToken());
      User user = userRepository.findByEmail(email).orElseThrow(
          () -> {
            log.warn("User not found for email: {}", email);
            throw new BusinessException(NOT_FOUND_MEMBER);
          });

      // AccessToken 재발급

      // User의 role -> 스프링시큐리티의 GrantedAuthority로 변경
      // 여러개의 role을 가질수 있으므로 Set
      UserDetails userDetails = new org.springframework.security.core.userdetails.User(
          user.getEmail(),
          user.getPassword(),
          Set.of(SecurityUtils.convertToAuthority(user.getRole()))
      );

      log.info("Successfully reissued access token for user: {}", user.getEmail());
      return jwtTokenProvider.generateAccessToken(userDetails);
    }
  }

  public void logout(TokenInfo logoutRequestDto) {
    if (!jwtTokenProvider.isValidAccessToken(logoutRequestDto.getAccessToken())) {
      log.warn("Invalid access token detected during logout: {}",
          logoutRequestDto.getAccessToken());
      throw new BusinessException(INVALID_ACCESS_TOKEN);
    }

    if (redisUtil.get("RT:" + logoutRequestDto.getRefreshToken()) != null) {
      redisUtil.delete("RT:" + logoutRequestDto.getRefreshToken());
    }

    Long expiration = jwtTokenProvider.getExpiration(logoutRequestDto.getAccessToken());
    redisUtil.setBlackList("AT:" + logoutRequestDto.getAccessToken(), "logout", expiration);

    log.info("Logout successful for accessToken: {}", logoutRequestDto.getAccessToken());
  }
}
