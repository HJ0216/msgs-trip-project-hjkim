package com.msgs.global.common.jwt;

import com.msgs.domain.user.domain.User;
import com.msgs.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  // UserDetailsService: Spring Security에서 유저 정보를 가져오는 Interface
  // 기본 Override Method: loadUserByUsername
  // loadUserByUsername: 유저 정보를 가져와 UserDetails로 return

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email).orElseThrow(() ->
        new UsernameNotFoundException("Authentication failed"));

    return UserPrinciple.builder()
                        .user(user)
                        .build();
  }
}
