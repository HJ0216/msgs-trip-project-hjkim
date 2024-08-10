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


    // ============================================================================

    @GetMapping(value = "/new/{userId}")
    public TokenResponse createToken(@PathVariable("userId") String userId) throws Exception {
        String token = jwtTokenProvider.createToken(userId); // 토큰 생성
        Claims claims = jwtTokenProvider.parseJwtToken("Bearer "+ token); // 토큰 검증

        TokenDataResponse tokenDataResponse = new TokenDataResponse(token, claims.getSubject(), claims.getIssuedAt().toString(), claims.getExpiration().toString());
        TokenResponse tokenResponse = new TokenResponse("200", "OK", tokenDataResponse);

        return tokenResponse;
    }

    @GetMapping(value = "/auth")
    public TokenResponseNoData checkToken(@RequestHeader(value = "Authorization") String token) throws Exception {
        Claims claims = jwtTokenProvider.parseJwtToken(token);

        TokenResponseNoData tokenResponseNoData = new TokenResponseNoData("200", "success");
        return tokenResponseNoData;
    }

    @Data
    @AllArgsConstructor
    static class TokenResponse<T> {

        private String code;
        private String msg;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class TokenDataResponse {
        private String token;
        private String subject;
        private String issued_time;
        private String expired_time;
    }

    @Data
    @AllArgsConstructor
    static class TokenResponseNoData<T> {
        private String code;
        private String msg;
    }


}


