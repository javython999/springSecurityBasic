package com.study.springsecuritybasic.config.auth;

import com.study.springsecuritybasic.model.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * 시큐리티가 /login 주소 요청을 낚아채서 로그인 진행
 * 로그인 완료가 되면 시큐리티의 session을 만들어 준다. (Security ContextHolder)
 * 시큐리티의 session에 들어갈 수 있는 오프젝트는 정해져 있다.(Authentication 타입 객체)
 * Authentication 안에는 User정보가 있어야 한다.
 * User정보로 Authentication 객체 안에 넣을 수 있는 오브젝트도 정해져 있다.(UserDetails 타입 객체)
 *
 */
@Data
public class PrincipalDetails implements UserDetails, OAuth2User {

    private User user;

    public PrincipalDetails(User user) {
        this.user = user;
    }


    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    // 해당 유저의 권한을 return
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }


    // 계정 만료 여부
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    // 비밀번호 유효기간 아직 남아있니?
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    // 계정 유효 여부
    @Override
    public boolean isEnabled() {
        // 1년동안 로그인을 안하면 휴면 계정으로 전환
        // 현재시간 - 로그인 시간 -> 1년 초과시 return false
        return true;
    }

    @Override
    public String getName() {
        return user.getUsername();
    }
}
