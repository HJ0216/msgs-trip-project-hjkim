package com.msgs.global.common.jwt;

import com.msgs.domain.user.domain.User;
import java.util.Collection;
import java.util.Collections;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
@Builder
public class UserPrinciple implements UserDetails {

  private final User user;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // 계정의 권한 목록
/*    Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
    grantedAuthorities.add(new GrantedAuthority() {
      @Override
      public String getAuthority() {
        return user.getRole();
      }
    });

    return grantedAuthorities;*/

    // 단일 역할
    return Collections.singletonList(() -> user.getRole());
  }

  @Override
  public String getUsername() {
    return user.getEmail(); // 계정의 고유한 값 리턴
  }

  @Override
  public String getPassword() {
    return user.getPassword(); // 계정의 비밀번호 리턴
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
