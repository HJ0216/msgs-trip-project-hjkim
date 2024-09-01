package com.msgs.domain.user.controller;

import lombok.RequiredArgsConstructor;

import com.msgs.domain.user.dto.LoginRequestDTO;
import com.msgs.domain.user.dto.LogoutRequestDTO;
import com.msgs.domain.user.dto.UserDTO;
import com.msgs.domain.user.service.SmsService;
import com.msgs.global.common.jwt.TokenInfo;
import com.msgs.domain.user.domain.User;
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

    @PostMapping("/new")
    public String create(@RequestBody User user){
        Integer id = userService.create(user);
        return id.toString();
    }

    @PostMapping("/new/sms-verification")
    public String verifySms(@RequestBody String phone) throws ParseException {
        Random random = new Random();
        String numStr = "";
        for (int i = 0; i < 6; i++) {
            String ran = Integer.toString(random.nextInt(10));
            numStr += ran;
        }
        smsService.sendSms(phone, numStr); //send authentication number

        return numStr;
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

    @PostMapping("/re-issue")
    @ResponseStatus(HttpStatus.OK)
    public TokenInfo reissue(@RequestBody LogoutRequestDTO logoutRequestDto) {
        return userService.reissue(logoutRequestDto);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@RequestBody LogoutRequestDTO logoutRequestDto){
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
