package com.msgs.domain.user.controller;

import com.msgs.domain.user.dto.UserDTO;
import com.msgs.domain.user.dto.request.LoginRequestDTO;
import com.msgs.domain.user.dto.request.SignUpRequestDTO;
import com.msgs.domain.user.dto.request.UpdateUserNicknameRequestDTO;
import com.msgs.domain.user.dto.request.UpdateUserPasswordRequestDTO;
import com.msgs.domain.user.service.UserService;
import com.msgs.global.common.jwt.TokenInfo;
import jakarta.validation.Valid;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

  private final Random random = new Random();

  @PostMapping("/new")
  @ResponseStatus(HttpStatus.CREATED)
  // ResponseEntity에서도 status를 지정하고 @ResponseStatus도 있다면 ResponseEntity가 우선순위를 갖
  public void create(@Valid @RequestBody SignUpRequestDTO signUpRequestDTO) {
    userService.create(signUpRequestDTO);
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public TokenInfo login(@Valid @RequestBody LoginRequestDTO loginRequestDto) {
    return userService.login(loginRequestDto);
  }

  @GetMapping("/me")
  @ResponseStatus(HttpStatus.OK)
  public UserDTO findMyInfo() {
    return userService.findMyInfo();
  }

  @PostMapping("/reissue")
  @ResponseStatus(HttpStatus.OK)
  public TokenInfo reissue(@RequestBody TokenInfo reissueRequestDto) {
    return userService.reissue(reissueRequestDto);
  }

  @PostMapping("/logout")
  @ResponseStatus(HttpStatus.OK)
  public void logout(@RequestBody TokenInfo logoutRequestDto) {
    userService.logout(logoutRequestDto);
  }

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
