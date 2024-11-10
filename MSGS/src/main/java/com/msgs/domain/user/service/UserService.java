package com.msgs.domain.user.service;

import static com.msgs.domain.user.exception.UserErrorCode.DUPLICATED_EMAIL;
import static com.msgs.domain.user.exception.UserErrorCode.DUPLICATED_PHONE_NUMBER;
import static com.msgs.domain.user.exception.UserErrorCode.EXPIRED_JWT;
import static com.msgs.domain.user.exception.UserErrorCode.INVALID_REFRESH_TOKEN;
import static com.msgs.domain.user.exception.UserErrorCode.NOT_FOUND_MEMBER;
import static com.msgs.domain.user.exception.UserErrorCode.REFRESH_TOKEN_IS_NULL;

import com.msgs.domain.user.domain.User;
import com.msgs.domain.user.dto.UserDTO;
import com.msgs.domain.user.dto.request.SignUpRequestDTO;
import com.msgs.domain.user.repository.UserRepository;
import com.msgs.global.common.error.BusinessException;
import com.msgs.global.common.jwt.JWTUtils;
import com.msgs.global.common.jwt.TokenInfo;
import com.msgs.global.common.redis.RedisUtils;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

  private static final long ACCESS_TOKEN_EXPIRY = 600000L; // 10분
  private static final long REFRESH_TOKEN_EXPIRY = 3600000L; // 1시간

  private final AuthenticationManagerBuilder authenticationManagerBuilder;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final UserRepository userRepository;
  private final JWTUtils jwtUtils;
  private final RedisUtils redisUtils;

  @Transactional
  public void create(SignUpRequestDTO signUpRequestDTO) {
    emailDuplicateCheck(signUpRequestDTO.getEmail());
    phoneDuplicateCheck(signUpRequestDTO.getPhone());
    userRepository.save(signUpRequestDTO.toEntity(bCryptPasswordEncoder));

    log.info("User successfully created for email: {}", signUpRequestDTO.getEmail());
  }

  public void emailDuplicateCheck(String email) {
    if (userRepository.findByEmail(email).isPresent()) {
      log.info("Email duplication check failed. Email: {}", email);
      throw new BusinessException(DUPLICATED_EMAIL);
    }
  }

  private void phoneDuplicateCheck(String phone) {
    if (userRepository.findByPhone(phone).isPresent()) {
      log.info("Phone duplication check failed. Phone: {}", phone);
      throw new BusinessException(DUPLICATED_PHONE_NUMBER);
    }
  }

/*  public TokenInfo login(LoginRequestDTO loginRequestDTO) {
    User user = userRepository.findByEmail(loginRequestDTO.getEmail()).orElseThrow(
        () -> {
          log.info("User not found for email: {}", loginRequestDTO.getEmail());
          throw new BusinessException(NOT_FOUND_MEMBER);
        });

    // 비밀번호 일치 여부 비교
    if (!loginRequestDTO.getPassword().equals(user.getPassword())) {
      log.info("Password validation failed for user: {}", loginRequestDTO.getEmail());
      throw new BusinessException(CHECK_LOGIN_ID_OR_PASSWORD);
    }

    // SecurityContextHolder에 저장
    // authenticate(): 전달된 인증 객체(Authentication)를 사용하여 인증되지 않은 사용자 인증을 수행
    // 내부적으로 설정된 AuthenticationProvider들을 순차적으로 사용하여 자격 증명을 검증
    // AuthenticationProvider: UserDetailsService 호출
    // UserDetailsService: 데이터베이스에서 해당 이메일을 가진 사용자를 찾으면, 그 사용자 정보(UserDetails)를 반환
    // * 해당 이메일이 존재하지 않는다면, UsernameNotFoundException이 발생하여 인증 실패
    Authentication authentication = authenticationManagerBuilder
        .getObject()
        .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),
            loginRequestDTO.getPassword()));

    UserDetails userDetails = (UserDetails) authentication.getPrincipal();

    TokenInfo tokenInfo = jwtTokenProvider.generateToken(userDetails);

    log.info("Generating token for user: {}", user.getEmail());

    return tokenInfo;
  }*/

  public UserDTO findMyInfo() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
        () -> {
          log.info("User not found for email: {}", authentication.getName());
          throw new BusinessException(NOT_FOUND_MEMBER);
        });

    return UserDTO.toUserDTO(user);
  }

/*  public TokenInfo reissue(TokenInfo reissueRequestDto) {
    try {
      jwtTokenProvider.getExpiration(reissueRequestDto.getAccessToken());

      log.info("Valid access token used for reissue: {}", reissueRequestDto.getAccessToken());
      throw new BusinessException(VALID_ACCESS_TOKEN);
    } catch (ExpiredJwtException e) {
      // Redis에 Refresh Token 존재 확인
      boolean hasStoredRefreshToken = redisUtils.hasKey(
          "RT:" + reissueRequestDto.getRefreshToken());
      if (!hasStoredRefreshToken) {
        log.info("Refresh token not found or expired in Redis. RefreshToken: {}",
            reissueRequestDto.getRefreshToken());
        throw new BusinessException(LOGOUT_MEMBER);
      }

      String email = (String) redisUtils.get("RT:" + reissueRequestDto.getRefreshToken());
      User user = userRepository.findByEmail(email).orElseThrow(
          () -> {
            log.info("User not found for email: {}", email);
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
  }*/

  public TokenInfo reissueToken(String refreshToken) {
    try {
      jwtUtils.isExpired(refreshToken);
    } catch (ExpiredJwtException e) {
      log.info("Refresh token expired: {}", refreshToken);
      throw new BusinessException(EXPIRED_JWT);
    }

    String category = jwtUtils.getCategory(refreshToken);

    if (!category.equals("refresh")) {
      log.info("Invalid refresh token detected: {}", refreshToken);
      throw new BusinessException(INVALID_REFRESH_TOKEN);
    }

    // Redis에 RT 저장 확인
    boolean isExistRefreshToken = redisUtils.hasKey("RT:" + refreshToken);
    if (!isExistRefreshToken) {
      log.info("Refresh token not found or expired in Redis: {}", refreshToken);
      throw new BusinessException(REFRESH_TOKEN_IS_NULL);
    }

    String username = jwtUtils.getUsername(refreshToken);
    String role = jwtUtils.getRole(refreshToken);

    String newAccessToken = jwtUtils.generateJwt("access", username, role, ACCESS_TOKEN_EXPIRY);
    String newRefreshToken = jwtUtils.generateJwt("refresh", username, role, REFRESH_TOKEN_EXPIRY);

    // Redis에 새로운 Token 값 저장
    redisUtils.delete("RT:" + refreshToken);
    redisUtils.set("RT:" + newRefreshToken, username, REFRESH_TOKEN_EXPIRY);

    log.info("New access and refresh tokens generated for user: {}", username);

    return TokenInfo.builder()
                    .grantType("Bearer ")
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .build();
  }

/*  public void logout(TokenInfo logoutRequestDto) {
    if (!jwtTokenProvider.isValidAccessToken(logoutRequestDto.getAccessToken())) {
      log.warn("Invalid access token detected during logout: {}",
          logoutRequestDto.getAccessToken());
      throw new BusinessException(INVALID_ACCESS_TOKEN);
    }

    if (redisUtils.get("RT:" + logoutRequestDto.getRefreshToken()) != null) {
      redisUtils.delete("RT:" + logoutRequestDto.getRefreshToken());
    }

    Long expiration = jwtTokenProvider.getExpiration(logoutRequestDto.getAccessToken());
    redisUtils.setBlackList("AT:" + logoutRequestDto.getAccessToken(), "logout", expiration);

    log.info("Logout successful for accessToken: {}", logoutRequestDto.getAccessToken());
  }*/

  @Transactional
  public void updateNickname(String newNickname) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
        () -> {
          log.warn("User not found for email: {}", authentication.getName());
          throw new BusinessException(NOT_FOUND_MEMBER);
        });

    user.setNickname(newNickname);

    userRepository.save(user);

    log.info("Nickname updated successfully. User email: {}, New nickname: {}",
        authentication.getName(), newNickname);
  }

  @Transactional
  public void updatePassword(String newPassword) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
        () -> {
          log.warn("User not found for email: {}", authentication.getName());
          throw new BusinessException(NOT_FOUND_MEMBER);
        });

    user.setPassword(newPassword);

    userRepository.save(user);

    log.info("Password updated successfully. User email: {}, New password: {}",
        authentication.getName(), newPassword);
  }
}
