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
                .allowedOrigins(
                        "http://localhost:8080",       // 本地开发
                        "http://localhost:8081",       // 本地 Nginx
                        "http://10.118.188.109:8080",  // 你电脑的 IP + 后端端口
                        "http://10.118.188.109:8081"   // 你电脑的 IP + Nginx 端口
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*") //允许前端传递请求头
                .allowCredentials(true);  //允许携带凭证
    }
}
