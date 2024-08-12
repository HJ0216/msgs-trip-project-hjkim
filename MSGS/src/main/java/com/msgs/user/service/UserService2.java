package com.msgs.user.service;

import com.msgs.msgs.dto.LoginRequestDTO;
import com.msgs.msgs.dto.TokenInfo;
import com.msgs.msgs.entity.user.LoginType;
import com.msgs.msgs.entity.user.User;
import com.msgs.msgs.error.BusinessException;
import com.msgs.msgs.jwt.JwtTokenProvider;
import com.msgs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.msgs.msgs.error.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService2 {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Integer create(User user){
        emailDuplicateCheck(user.getEmail());
        userRepository.save(user);

        return user.getId();
    }

    public void emailDuplicateCheck(String email){
        if(!userRepository.findByEmail(email).isEmpty()){
            throw new BusinessException(DUPLICATED_EMAIL);
        }
    }

    public TokenInfo login(LoginRequestDTO loginRequestDTO){
        if(loginRequestDTO.getLoginType() == LoginType.KAKAO){
            User user = User.kakaoCreate(loginRequestDTO);

            // 동일한 Email , LoginID 일 경우 토큰만 발급 후 리턴
            if (userRepository.findByEmail(loginRequestDTO.getEmail()).isPresent()) {
                return jwtTokenProvider.generateToken(loginRequestDTO);
            }

            userRepository.save(user);

            return jwtTokenProvider.generateToken(loginRequestDTO);
        }

        // 일반
        User user = userRepository.findById(loginRequestDTO.getId()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));

        // 비밀번호 일치 여부 비교 ( 로그인 요청한 PW == DB 암호화 비밀번호 )
        if(loginRequestDTO.getPassword().equals(user.getPassword())) {
            throw new BusinessException(NOT_EQUAL_PASSWORD);
        }

        return jwtTokenProvider.generateToken(loginRequestDTO);
    }
}
