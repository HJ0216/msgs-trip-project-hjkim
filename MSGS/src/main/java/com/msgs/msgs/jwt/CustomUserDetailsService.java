package com.msgs.msgs.jwt;

import com.msgs.msgs.dto.UserPrinciple;
import com.msgs.msgs.entity.user.User;
import com.msgs.msgs.error.BusinessException;
import com.msgs.user.repository.UserRepository;
import com.msgs.user.service.UserService2;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.msgs.msgs.error.ErrorCode.NOT_FOUND_MEMBER;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    // UserDetailsService: Spring Security에서 유저 정보를 가져오는 Interface
    // 기본 Override Method: loadUserByUsername
    // loadUserByUsername: 유저 정보를 가져와 UserDetails로 return

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(()->
                new BusinessException(NOT_FOUND_MEMBER));

        //user의 role을 스프링시큐리티의 GrantedAuthority로 바꿔준다. 여러개의 role을 가질수 있으므로 Set
        Set<GrantedAuthority> authorities = Set.of(SecurityUtils.convertToAuthority(user.getRole()));

        return UserPrinciple.builder()
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword())) // PasswordEncoder를 통해 UserDetails 객체를 생성할 때 encoding
                .user(user)
                .authorities(authorities)
                .build();
    }
}
