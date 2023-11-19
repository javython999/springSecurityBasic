package com.study.springsecuritybasic.config.oauth.privider;

public interface OAuth2UserInfo {
    String getProviderId();
    String getProvider();
    String getEmaill();
    String getName();
}
