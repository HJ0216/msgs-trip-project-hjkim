package com.msgs.msgs.jwt.controller;

import com.msgs.msgs.dto.TokenInfo;
import com.msgs.msgs.dto.UserLoginRequestDto;
import com.msgs.msgs.jwt.JwtTokenProvider;
import com.msgs.msgs.jwt.service.UserService;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/token")
public class JwtController {

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

    @PostMapping("/info")
    public ResponseEntity<?> getUserInfo(@RequestParam String accessToken) {
        JSONObject userInfo = userService.getUserInfo(accessToken);
        return ResponseEntity.ok().body(userInfo.toString());
    }
}


