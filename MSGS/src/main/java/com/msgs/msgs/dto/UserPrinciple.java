package com.msgs.msgs.dto;

import com.msgs.msgs.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPrinciple implements UserDetails {
    private String email;
    transient private String password; // 직렬화 과정에서 제외
    transient private User user; // 직렬화 과정에서 제외
    private Set<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities; // 계정의 권한 목록
    }

    @Override
    public String getUsername() {
        return email; // 계정의 고유한 값 리턴
    }

    @Override
    public String getPassword() {
        return password; // 계정의 비밀번호 리턴
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
