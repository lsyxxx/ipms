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
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
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

    /**
     * 白名单
     * 不需要认证&授权
     *
     * @return String[]
     */
    public static String[] whiteList() {
        return new String[]{
                "/", "/home", "/error",
                //swagger
//                "/swagger-ui.html", "/swagger-ui/**", "/swagger-resource/**", "/v3/api-docs/**", "/v2/api-docs/**", "/webjars/**", "/doc.html",
                //测试使用
//                "/test/**",
//                "/static/**", //静态资源
//                "/camunda/**",
//                "/favicon.ico",
                //TEST
//                "/test/**",
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        RequestCache nullRequestCache = new NullRequestCache();
        http.authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(new AntPathRequestMatcher("/test/**"), new AntPathRequestMatcher("/public/**"),
                                new AntPathRequestMatcher("/camunda/**"), new AntPathRequestMatcher("/favicon.ico")).permitAll()
                        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                        .anyRequest()
                        .access(abtAuthorizationManager)
                )

                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .anonymous(AbstractHttpConfigurer::disable)

                .addFilterBefore(this.abtWebApiTokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(abtWebApiTokenAuthenticationProvider)
                .exceptionHandling(config -> config
                        .accessDeniedHandler(tokenAuthenticationHandler)
                        .authenticationEntryPoint(tokenAuthenticationHandler))

                .requestCache((cache) -> cache
                        .requestCache(nullRequestCache))
                .sessionManagement(session -> {
                             session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                             //每个用户最多一个session
//                             session.maximumSessions(1);
//                             session.invalidSessionUrl("/test/sl/session/invalid");
                        }

                )


        ;
        return http.build();
    }

    /**
     * 用户认证管理
     *
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


}
