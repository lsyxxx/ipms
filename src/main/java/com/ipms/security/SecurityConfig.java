package com.ipms.security;

import com.ipms.common.model.R;
import com.ipms.sys.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import java.io.PrintWriter;

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
                        //白名单不需要认证
                        .requestMatchers("/", "/home", "/error", "/invalidSession", "/failLogin", "/toLogin")
                        .permitAll()
                        //需要权限
                        .requestMatchers("/u/*").hasRole("ADMIN")
                        //测试路径不需要认证
                        .requestMatchers("/test/*").permitAll()
                        //认证
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .formLogin(withDefaults())
                .formLogin((config) -> config
//                        .loginPage("/toLogin")
//                        .loginProcessingUrl("/doLogin")
                        .successHandler(new SuccessLoginHandler())
                        .failureHandler((request, response, authenticationException) -> {
                            log.info("formLogin.failureHandler()");
                            ResponseUtil.returnJson(response, R.fail("login fail...").toString());
                        })
                        //the URL to validate username and password
                        .loginProcessingUrl("/doLogin"))
                //认证异常处理
                .exceptionHandling(config -> config
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            log.info("exceptionHandling.accessDeniedHandler()");
                            PrintWriter writer = response.getWriter();
                            writer.write(R.fail("accessDeniedHandler fail..").toString());
                            writer.flush();
                            writer.close();
                        })
//                        .authenticationEntryPoint(((request, response, authException) -> {
//                            log.info("formLogin.authenticationEntryPoint()");
//                            PrintWriter writer = response.getWriter();
//                            writer.write(R.fail("entrypoint fail..").toString());
//                            writer.flush();
//                            writer.close();
//                        }))
                )


//                防止请求被保存，比如没有认证的用户信息不保存在session中
                .requestCache((cache) -> cache
                        .requestCache(nullRequestCache))
                //设置session超时
                .sessionManagement(session -> session
                        //超时跳转url
                        .invalidSessionUrl("/invalidSession"))
                //开启跨域请求，测试使用
                .cors(AbstractHttpConfigurer::disable)
                //开启模拟请求，测试使用
                .csrf(AbstractHttpConfigurer::disable)
        //持久化securityContext
//                .securityContext((securityContext) -> securityContext
//                                .securityContextRepository(new RequestAttributeSecurityContextRepository()))


//                //addFilterBefore: 在 AuthorizationFilter 之前添加自定义filter
//                .addFilterBefore(new UserFuncFilter(), AuthorizationFilter.class)
        ;
        return http.build();
    }

    // @formatter:off
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER", "ACC")
//                .build();
//        return new InMemoryUserDetailsManager(user);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
