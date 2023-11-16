package com.study.springsecuritybasic.config;

import com.study.springsecuritybasic.config.oauth.PrincipalOauth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록된다.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured 애노테이션 활성화, preAuthorize, postAuthorize 애노테이션 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }

    /**
     * oauth 과정
     * 1. 코드 받기(인증)
     * 2. 엑세스 토큰 받기(사용자 정보에 접근 권한)
     * 3. 사용자 프로필 정보 획득
     * 4. 회원가입 자동 진행 or 추가정보 입력받아 회원가입 진행
     * # 구글oauth의 경우 코드를 받는게 아니라 (엑세스 토큰 + 프로필)을 한번에 받는다
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/user/**").authenticated() // 인증만되면 접근 가능 페이지
                .antMatchers("/manager/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login") // /login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인을 진행해줌 -> 로그인 진행 페이지를 컨트롤러에 안만들어도 됨
                .defaultSuccessUrl("/")
                .and()
                .oauth2Login()
                .loginPage("/loginForm") // 구글 로그인 완료후 후처리 필요
                .userInfoEndpoint()
                .userService(principalOauth2UserService)
        ;

    }
}
