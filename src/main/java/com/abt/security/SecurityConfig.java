package com.abt.security;

import com.abt.http.dto.WebApiToken;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
    private final ABTAuthorizationManager abtAuthorizationManager;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        RequestCache nullRequestCache = new NullRequestCache();
        http
                //授权，而不是认证
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(whiteList()).permitAll()
                        //应用内转发不需要授权
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .anyRequest()
//                        .authenticated()
                        .access(abtAuthorizationManager)
                )

                .formLogin(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)

                .addFilterBefore(this.abtWebApiTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

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
     * @return ProviderManager
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        log.info("注册authenticationManager");
        return new ProviderManager(abtWebApiTokenAuthenticationProvider);
    }

    public ABTWebApiTokenAuthenticationFilter abtWebApiTokenAuthenticationFilter() {
        return new ABTWebApiTokenAuthenticationFilter(this.authenticationManager(), tokenAuthenticationHandler, WebApiToken.of());
    }


    /**
     * 白名单
     * 不需要认证&授权
     * @return String[]
     */
    public static String[] whiteList() {
        return new String[]{
                "/", "/home", "/error",
                //swagger
                "/swagger-ui.html", "/swagger-ui/**", "/swagger-resource/**", "/v3/api-docs/**", "/v2/api-docs/**", "/webjars/**", "/doc.html",
                //测试使用
                "/test/**",
                "/static/**", //静态资源
        };
    }


}
