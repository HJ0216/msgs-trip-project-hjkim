package com.msgs.global.common.jwt.controller;

import com.msgs.global.common.jwt.JwtTokenProvider;
import com.msgs.global.common.jwt.dto.TokenInfo;
import com.msgs.domain.user.dto.UserLoginRequestDto;
import com.msgs.global.common.jwt.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public TokenInfo login(@RequestBody UserLoginRequestDto userLoginRequestDto) {

//        String userId = userLoginRequestDto.getId();
        String userEmail = userLoginRequestDto.getEmail();
        String password = userLoginRequestDto.getPassword();
        System.out.println(userEmail);
        System.out.println(password);

        TokenInfo tokenInfo = userService.login(userEmail, password);
        System.out.println("jjjjjjjjjjjjjjjjjjjj"+ tokenInfo);

        return tokenInfo;
    }

//    @GetMapping
//    public ResponseEntity<?> getUserInfo(@RequestParam String accessToken) {
//        JSONObject userInfo = userService.getUserInfo(accessToken);
//        return ResponseEntity.ok().body(userInfo.toString());
//    }
}


