package com.ipms.security;

import com.ipms.common.model.R;
import com.ipms.sys.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthorizationManager<RequestAuthorizationContext> authz) throws Exception {
        RequestCache nullRequestCache = new NullRequestCache();
        http
                .authorizeHttpRequests((authorize) -> authorize
                        // !url通配符为**，/test/**表示/test下所有资源
                        // whitelist
                        .requestMatchers("/", "/home", "/error", "/invalidSession","/doLogin").permitAll()
                        //swagger
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/swagger-resource/**", "/v3/api-docs/**", "/v2/api-docs/**", "/webjars/**", "/doc.html").permitAll()
                        //测试路径不需要认证
                        .requestMatchers("/test/**").permitAll())
                .requestCache((cache) -> cache
                        .requestCache(nullRequestCache))
                //设置session超时
                //TODO: 超时重新请求，强制刷新
//                .sessionManagement(session -> session
                        //超时跳转url redirect to webapi
//                        .invalidSessionUrl("/invalidSession"))
                //开启跨域请求，测试使用
                .cors(AbstractHttpConfigurer::disable)
                //开启模拟请求，测试使用
                .csrf(AbstractHttpConfigurer::disable)

                //认证异常处理
                .exceptionHandling(config -> config
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
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



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
