package com.xy.crm.config;

import com.xy.crm.interceptor.NologinInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {


    @Bean
    public NologinInterceptor nologinInterceptor(){
        return new NologinInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(nologinInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/index","/user/login","/css/**","/images/**","/js/**","/lib/**");

    }
}
