package com.msgs.domain.user.controller;

import static com.msgs.domain.user.exception.UserErrorCode.INVALID_ACCESS_TOKEN;
import static com.msgs.domain.user.exception.UserErrorCode.REFRESH_TOKEN_IS_NULL;

import com.msgs.domain.user.dto.UserDTO;
import com.msgs.domain.user.dto.request.SignUpRequestDTO;
import com.msgs.domain.user.dto.request.UpdateUserNicknameRequestDTO;
import com.msgs.domain.user.dto.request.UpdateUserPasswordRequestDTO;
import com.msgs.domain.user.service.UserService;
import com.msgs.global.common.error.BusinessException;
import com.msgs.global.common.jwt.TokenInfo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Collection;
import java.util.Iterator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("api/v2/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/new")
  @ResponseStatus(HttpStatus.CREATED)
  // ResponseEntity에서도 status를 지정하고 @ResponseStatus도 있다면 ResponseEntity가 우선순위를 갖
  public void create(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO) {
    userService.create(signUpRequestDTO);
  }

/*  LoginFilter로 대체

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public TokenInfo login(@Valid @RequestBody LoginRequestDTO loginRequestDto) {
    return userService.login(loginRequestDto);
  }*/

  @GetMapping("/my")
  @ResponseStatus(HttpStatus.OK)
  public UserDTO findMyInfo() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    String username = authentication.getName();

    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
    GrantedAuthority authority = iterator.next();
    String role = authority.getAuthority();

    log.info("username: {}, role: {}", username, role);

    return userService.findMyInfo();
  }

/*  re-issue API로 대체

  @PostMapping("/reissue")
  @ResponseStatus(HttpStatus.OK)
  public TokenInfo reissue(@RequestBody TokenInfo reissueRequestDto) {
    return userService.reissue(reissueRequestDto);
  }*/

  @PostMapping("/re-issue")
  @ResponseStatus(HttpStatus.OK)
  public void reIssue(HttpServletRequest request, HttpServletResponse response) {
    // Access token 유효성 검사
    String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (accessToken == null || !accessToken.startsWith("Bearer ")) {
      throw new BusinessException(INVALID_ACCESS_TOKEN);
    }

    accessToken = accessToken.substring(7); // "Bearer " 제거
    log.info("Received Access Token: {}", accessToken);

    String refreshToken = null;

    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals("refresh")) {
        refreshToken = cookie.getValue();
      }
    }

    if (refreshToken == null) {
      throw new BusinessException(REFRESH_TOKEN_IS_NULL);
    }

    TokenInfo tokenInfo = userService.reissueToken(accessToken, refreshToken);
    response.setHeader(HttpHeaders.AUTHORIZATION,
        tokenInfo.getGrantType() + tokenInfo.getAccessToken());
    response.addCookie(createCookie("refresh", tokenInfo.getRefreshToken()));
  }

  private Cookie createCookie(String key, String value) {
    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(1 * 10 * 60); // 10분
//    cookie.setSecure(true); // 쿠키가 HTTPS 연결을 통해서만 전송되도록 설정
//    cookie.setPath("/"); // 쿠키가 사용할 수 있는 URL 경로를 지정
    cookie.setHttpOnly(true);
    // 쿠키를 JavaScript와 같은 클라이언트 측, 스크립트에서 접근할 수 없도록 설정
    // JavaScript의 document.cookie를 사용해 쿠키의 값을 읽거나 수정할 수 없음
    // XSS(크로스 사이트 스크립팅) 공격으로부터 쿠키를 보호하는 데 도움

    return cookie;
  }

/*  LogoutFilter로 대체

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.OK)
  public void logOut(@RequestBody TokenInfo logoutRequestDto) {
    userService.logout(logoutRequestDto);
  }*/

  @PatchMapping("/nickname")
  @ResponseStatus(HttpStatus.OK)
  public void updateNickname(@Valid @RequestBody UpdateUserNicknameRequestDTO request) {
    userService.updateNickname(request.getNickname());
  }

  @PatchMapping("/password")
  @ResponseStatus(HttpStatus.OK)
  public void patchPassword(@Valid @RequestBody UpdateUserPasswordRequestDTO request) {
    userService.updatePassword(request.getPassword());
  }

  @DeleteMapping()
  public void deleteAccount() {

  }
}
