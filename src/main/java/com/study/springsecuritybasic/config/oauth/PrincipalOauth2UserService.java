package com.study.springsecuritybasic.config.oauth;

import com.study.springsecuritybasic.config.auth.PrincipalDetails;
import com.study.springsecuritybasic.config.oauth.privider.*;
import com.study.springsecuritybasic.model.User;
import com.study.springsecuritybasic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserRepository userRepository;

    // 구글로부터 받은 userRequest data에 대한 후처리 함수
    // 메서드 종료시 @AuthenticationPrincipal 애노테이션 생성
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        System.out.println("    userRequest : " + userRequest);
        System.out.println("    getClientRegistration : " + userRequest.getClientRegistration());
        System.out.println("    getAccessToken : " + userRequest.getAccessToken().getTokenValue());

        /**
         * 구글 로그인 버튼 -> 구글 로그인 창 -> 로그인 완료 ->
         * code를 리턴(OAuth-client라이브러리) -> AccessToken요청 = userRequest정보
         * userRequest정보로 회원 프로필 받아야함(loadUser() 호출) -> 구글로부터 회원 프로필 받음
         */
        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println("super.loadUser(userRequest).getAttributes : " + oAuth2User.getAttributes());

        OAuth2UserInfo oAuth2UserInfo = null;
        if("google".equals(userRequest.getClientRegistration().getRegistrationId())) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if ("facebook".equals(userRequest.getClientRegistration().getRegistrationId())) {
            oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
        } else if ("naver".equals(userRequest.getClientRegistration().getRegistrationId())) {
            oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
        } else if ("kakao".equals(userRequest.getClientRegistration().getRegistrationId())) {
            Map<String, Object> attribute = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            attribute.put("id", oAuth2User.getAttributes().get("id").toString());
            oAuth2UserInfo = new KakaoUserInfo(attribute);
        } else {
            System.out.println("현재 OAuth는 google, facebook, naver, kakao만 지원합니다.");
        }

        // OAuth 로그인 유저 회원가입 진행
        String username = oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId();
        User userEntity = userRepository.findByUsername(username);
        if(userEntity == null) {
            userEntity = User.builder()
                            .username(username)
                            .password(bCryptPasswordEncoder.encode("OAuthUser"))
                            .email(oAuth2UserInfo.getEmaill())
                            .role("ROLE_USER")
                            .provider(oAuth2UserInfo.getProvider())
                            .providerId(oAuth2UserInfo.getProviderId())
                            .build();
            userRepository.save(userEntity);
        }

        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
