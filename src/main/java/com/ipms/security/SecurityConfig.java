package com.ipms.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import static org.springframework.security.config.Customizer.withDefaults;


/**
 * spring security config
 */

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        RequestCache nullRequestCache = new NullRequestCache();
        http
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers("/", "/home", "/error", "/invalidSession").permitAll()
                        .requestMatchers("/u/*").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .formLogin(withDefaults())
//                .formLogin((config) -> config
//                        .failureForwardUrl("/failLogin"))
//                防止请求被保存，比如没有认证的用户信息不保存在session中
                .requestCache((cache) -> cache
                        .requestCache(nullRequestCache))
                //设置session超时
                .sessionManagement(session -> session
                        //超时跳转url
                        .invalidSessionUrl("/invalidSession"))
                //持久化securityContext
                .securityContext((securityContext) -> securityContext
                                .securityContextRepository(new RequestAttributeSecurityContextRepository()))


//                //addFilterBefore: 在 AuthorizationFilter 之前添加自定义filter
//                .addFilterBefore(new UserFuncFilter(), AuthorizationFilter.class)
                ;
        return http.build();
    }

    // @formatter:off
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER", "ACC")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
