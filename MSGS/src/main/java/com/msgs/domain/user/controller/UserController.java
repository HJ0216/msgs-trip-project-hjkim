package com.msgs.domain.user.controller;

import com.msgs.domain.user.dto.LoginRequestDTO;
import com.msgs.domain.user.dto.SignUpRequestDTO;
import com.msgs.domain.user.dto.UserDTO;
import com.msgs.domain.user.service.SmsService;
import com.msgs.domain.user.service.UserService;
import com.msgs.global.common.jwt.TokenInfo;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v2/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final SmsService smsService;

  private final Random random = new Random();

  @PostMapping("/new")
  @ResponseStatus(HttpStatus.CREATED)
  public void create(@RequestBody SignUpRequestDTO dto) {
    dto.validUserDto();
    userService.create(dto);
  }

  @PostMapping("/new/sms-verification")
  public String verifySms(@RequestBody String phone) throws ParseException {
    StringBuilder randomSb = new StringBuilder();
    for (int i = 0; i < 6; i++) {
      randomSb.append(random.nextInt(10));
    }
    smsService.sendSms(phone, randomSb.toString()); //send authentication number

    return randomSb.toString();
  }

  @PostMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public TokenInfo login(@RequestBody LoginRequestDTO loginRequestDto) {
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
  public void patchNickname() {

  }

  @PatchMapping("/password")
  public void patchPassword() {

  }

  @DeleteMapping()
  public void deleteAccount() {

  }
}
