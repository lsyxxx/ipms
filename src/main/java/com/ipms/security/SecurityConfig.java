package com.ipms.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;


/**
 * spring security config
 */

@Configuration
@EnableWebSecurity
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityConfig {

    private final ABTWebApiTokenAuthenticationProvider abtWebApiTokenAuthenticationProvider;
    private final TokenAuthenticationHandler tokenAuthenticationHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        RequestCache nullRequestCache = new NullRequestCache();
        http
                //授权，而不是认证
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(whiteList()).permitAll()
                        .anyRequest().authenticated()
                )

                .formLogin(config -> config.disable())
                .anonymous(config -> config.disable())

                .addFilterBefore(this.abtWebApiTokenAuthenticationFilter(), AuthorizationFilter.class)

                .authenticationManager(this.userAuthenticationManager())
                .authenticationProvider(abtWebApiTokenAuthenticationProvider)
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(config -> config
                        .accessDeniedHandler(tokenAuthenticationHandler)
                        .authenticationEntryPoint(tokenAuthenticationHandler))


                .requestCache((cache) -> cache
                        .requestCache(nullRequestCache))
                .sessionManagement(session ->
                        //无状态session
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))


        ;
        return http.build();
    }


    /**
     * 用户认证管理
     * @return
     */
    @Bean
    public AuthenticationManager userAuthenticationManager() {
        log.info("注册userAuthenticationManager");
        return new ProviderManager(abtWebApiTokenAuthenticationProvider);
    }

    public ABTWebApiTokenAuthenticationFilter abtWebApiTokenAuthenticationFilter() {
        return new ABTWebApiTokenAuthenticationFilter();
    }


    /**
     * 白名单
     * 不需要认证&授权
     * @return
     */
    public static String[] whiteList() {
        return new String[]{
                "/", "/home", "/error",
                //swagger
                "/swagger-ui.html", "/swagger-ui/**", "/swagger-resource/**", "/v3/api-docs/**", "/v2/api-docs/**", "/webjars/**", "/doc.html",
                //测试使用
                "/test/**",
        };
    }


}
