package com.bingzhi.vinyl.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//后端加CORS配置，这样前端才能访问
//CORS（跨域共享资源）：浏览器的安全机制
@Configuration
public class CorConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**") //对所有接口都生效
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*") //允许前端传递请求头
                .allowCredentials(true);  //允许携带凭证
    }
}
