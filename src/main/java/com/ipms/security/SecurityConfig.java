package com.ipms.security;

import com.ipms.common.model.R;
import com.ipms.sys.util.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
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
                        //白名单不需要认证
                        .requestMatchers("/", "/home", "/error", "/invalidSession","/doLogin").permitAll()
                        //测试路径不需要认证
                        .requestMatchers("/test/*").permitAll()
                        //需要权限
                        //TODO 数据库或xml存储？
                        .requestMatchers("/u/*").hasRole("ADMIN")
                        //认证
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())

                //表单验证
                .formLogin((config) -> config
//                        .loginPage("/toLogin")
                        .successHandler(new UserLoginSuccessHandler())
                        .failureHandler(new UserLoginFailureHandler())
                        //the URL to validate username and password
                        .loginProcessingUrl("/doLogin")
                )

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
                //remember me

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
        //比如验证码认证
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
