package com.bingzhi.vinyl.config;

import com.bingzhi.vinyl.interceptor.AdminInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    //自动注入拦截器
    @Autowired
    private AdminInterceptor adminInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        //注册拦截器
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**"); //拦截这个开头的
    }
}
