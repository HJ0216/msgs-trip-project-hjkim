package com.msgs.domain.user.controller;

import com.msgs.domain.user.dto.SignUpRequestDTO;
import lombok.RequiredArgsConstructor;

import com.msgs.domain.user.dto.LoginRequestDTO;
import com.msgs.domain.user.dto.UserDTO;
import com.msgs.domain.user.service.SmsService;
import com.msgs.global.common.jwt.TokenInfo;
import com.msgs.domain.user.service.UserService;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

@RestController
@RequestMapping("api/v2/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final SmsService smsService;

    private Random random = new Random();

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody SignUpRequestDTO dto){
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
    public TokenInfo login(@RequestBody LoginRequestDTO loginRequestDto){
        return userService.login(loginRequestDto);
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserDTO findMyInfo(){
        return userService.findMyInfo();
    }

    @PostMapping("/reissue")
    @ResponseStatus(HttpStatus.OK)
    public TokenInfo reissue(@RequestBody TokenInfo reissueRequestDto) {
        return userService.reissue(reissueRequestDto);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@RequestBody TokenInfo logoutRequestDto){
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
