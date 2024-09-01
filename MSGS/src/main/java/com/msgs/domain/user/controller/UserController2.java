package com.msgs.domain.user.controller;

import com.msgs.domain.user.dto.LoginRequestDTO;
import com.msgs.domain.user.dto.LogoutRequestDTO;
import com.msgs.domain.user.dto.UserDTO;
import com.msgs.global.common.jwt.dto.TokenInfo;
import com.msgs.domain.user.domain.User;
import com.msgs.domain.user.service.UserService2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v2/users")
@RequiredArgsConstructor
public class UserController2 {
    private final UserService2 userService;

    @PostMapping("/new")
    public String create(@RequestBody User user){
        Integer id = userService.create(user);
        return id.toString();
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

}
