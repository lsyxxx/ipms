package com.ipms.security;

import com.ipms.common.model.R;
import com.ipms.sys.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.stereotype.Component;

import static org.springframework.security.config.Customizer.withDefaults;


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

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthorizationManager<RequestAuthorizationContext> authz) throws Exception {
        RequestCache nullRequestCache = new NullRequestCache();
        http
                //授权，而不是认证
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(whiteList()).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**")
                        .authenticated()
                )

                .addFilterBefore(this.abtWebApiTokenAuthenticationFilter(), AuthorizationFilter.class)
                .authenticationManager(this.userAuthenticationManager())
                .authenticationProvider(abtWebApiTokenAuthenticationProvider)



                .requestCache((cache) -> cache
                        .requestCache(nullRequestCache))
                .sessionManagement(session ->
                        //无状态session
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)


                //认证异常处理
                .exceptionHandling(config -> config
                        .accessDeniedHandler((request, response, authenticationException) -> {
                            log.info("exceptionHandling.accessDeniedHandler()");
                            ResponseUtil.returnJson(response, R.fail("exception: access denied...").toJson());
                        })
                        //未登录处理
                        .authenticationEntryPoint(((request, response, authException) -> {
                            log.info("please sign in...");
                            ResponseUtil.returnJson(response, R.fail("to sign in").toJson());
                        }))
                )
        ;
        return http.build();
    }

    /**
     * 一般表单登录
     * 暂时没用到
     * @param http
     * @return
     * @throws Exception
     */
//    @Bean
    public SecurityFilterChain formLoginSecurityFilterChain(HttpSecurity http) throws Exception {

        http.formLogin((config) -> config
//                        .loginPage("/toLogin")
                    .successHandler(new UserLoginSuccessHandler())
                    .failureHandler(new UserLoginFailureHandler())
                    //the URL to validate username and password
                    .loginProcessingUrl("/doLogin"))
        ;
        return http.build();
    }


    /**
     * 用户认证管理
     * @return
     */
    @Bean
    public AuthenticationManager userAuthenticationManager() {
        return new ProviderManager(abtWebApiTokenAuthenticationProvider);
    }

    @Bean
    public ABTWebApiTokenAuthenticationFilter abtWebApiTokenAuthenticationFilter() {
        ABTWebApiTokenAuthenticationFilter abtWebApiTokenAuthenticationFilter = new ABTWebApiTokenAuthenticationFilter();
        abtWebApiTokenAuthenticationFilter.setAuthenticationManager(userAuthenticationManager());
        return abtWebApiTokenAuthenticationFilter;
    }

    public static String[] whiteList() {
        return new String[]{
                "/", "/home",
                //swagger
                "/swagger-ui.html", "/swagger-ui/**", "/swagger-resource/**", "/v3/api-docs/**", "/v2/api-docs/**", "/webjars/**", "/doc.html",
                //测试使用
                "/test/**"
        };
    }




}
