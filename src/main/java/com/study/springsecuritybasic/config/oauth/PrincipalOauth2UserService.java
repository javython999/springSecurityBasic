package com.study.springsecuritybasic.config.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    // 구글로부터 받은 userRequest data에 대한 후처리 함수
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("userRequest : " + userRequest);
        System.out.println("getClientRegistration : " + userRequest.getClientRegistration());
        System.out.println("getAccessToken : " + userRequest.getAccessToken().getTokenValue());
        System.out.println("super.loadUser(userRequest).getAttributes : " + super.loadUser(userRequest).getAttributes());
        /**
         * 구글 로그인 버튼 -> 구글 로그인 창 -> 로그인 완료 ->
         * code를 리턴(OAuth-client라이브러리) -> AccessToken요청 = userRequest정보
         * userRequest정보로 회원 프로필 받아야함(loadUser() 호출) -> 구글로부터 회원 프로필 받음
         */

        OAuth2User oAuth2User = super.loadUser(userRequest);
        return super.loadUser(userRequest);
    }
}
