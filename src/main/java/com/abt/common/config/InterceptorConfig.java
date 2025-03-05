package com.abt.common.config;

import com.abt.common.interceptor.TokenRemoveInterceptor;
import com.abt.common.interceptor.TokenValidateInterceptor;
import com.abt.salary.SalarySessionInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置拦截器
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SalarySessionInterceptor())
                .addPathPatterns("/sl/my/**");
        registry.addInterceptor(new TokenValidateInterceptor())
                .addPathPatterns("/invoice/check/**");
        registry.addInterceptor(new TokenRemoveInterceptor())
                .addPathPatterns("/wf/rbs/apply")
                .addPathPatterns("/wf/rbs/restart");
    }


}
