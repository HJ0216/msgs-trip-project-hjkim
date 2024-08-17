package com.msgs.user.service;

import com.amazonaws.util.ImmutableMapParameter;
import com.msgs.msgs.dto.LoginRequestDTO;
import com.msgs.msgs.dto.TokenInfo;
import com.msgs.msgs.dto.UserDTO;
import com.msgs.msgs.dto.UserPrinciple;
import com.msgs.msgs.entity.user.LoginType;
import com.msgs.msgs.entity.user.User;
import com.msgs.msgs.error.BusinessException;
import com.msgs.msgs.jwt.JwtTokenProvider;
import com.msgs.msgs.jwt.SecurityUtils;
import com.msgs.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

import static com.msgs.msgs.error.ErrorCode.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService2 {
    private final AuthenticationManager authenticationManager;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

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

            Authentication authentication = authenticationManagerBuilder
                    .getObject()
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
//            );

            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();

            // 동일한 Email , LoginID 일 경우 토큰만 발급 후 리턴
            if (userRepository.findByEmail(loginRequestDTO.getEmail()).isPresent()) {
                jwtTokenProvider.generateToken(userPrinciple);
            }

            userRepository.save(user);

            return jwtTokenProvider.generateToken(userPrinciple);
        }

        // 일반
        User user = userRepository.findByEmail(loginRequestDTO.getEmail()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));

        // 비밀번호 일치 여부 비교
        if(!loginRequestDTO.getPassword().equals(user.getPassword())) {
            throw new BusinessException(NOT_EQUAL_PASSWORD);
        }

        //스프링 시큐리티에서 로그인하기
        Authentication authentication = authenticationManagerBuilder
                .getObject()
                .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
//        );


        //user의 role을 스프링시큐리티의 GrantedAuthority로 바꿔준다. 여러개의 role을 가질수 있으므로 Set
        Set<GrantedAuthority> authorities = Set.of(SecurityUtils.convertToAuthority(user.getRole()));

        UserPrinciple userPrinciple = UserPrinciple.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .user(user)
                .build();

        return jwtTokenProvider.generateToken(userPrinciple);
    }

    public UserDTO findMyInfo(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(
                () -> new BusinessException(NOT_FOUND_MEMBER));

        return UserDTO.toUserDTO(user);
    }
}
