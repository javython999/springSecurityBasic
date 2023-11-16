package com.study.springsecuritybasic.config.auth;

import com.study.springsecuritybasic.model.User;
import com.study.springsecuritybasic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 시큐리티 설정에서 loginProcessingUrl("/login"); 요청이 오면
 * 자동으로 UserDetailsService 타입으로 IoC되어있는 loadUserByUsername 함수가 실행된다.
 */
@Service
public class PrincipalDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;


    /**
     * 시큐리티 session <= Authentication <= UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findByUsername(username);

        if (userEntity != null) {
            return new PrincipalDetails(userEntity);
        }
        return null;
    }
}
