package com.bingzhi.vinyl.interceptor;

import com.bingzhi.vinyl.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    //在请求处理之前执行
    //return true放行 false拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception{
        //获取请求的URI
        String uri=request.getRequestURI();

        //登录接口不需要验证，直接放行
        if(uri.contains("/admin/employee/login")){
            return true;
        }

        //从请求体获取token
        String token=request.getHeader("Authorization");
        System.out.println("收到的token: " + token);

        //验证token
        if (token==null || !JwtUtil.validateToken(token)){
            //设置1响应状态码为401(未授权)
            response.setStatus(401);
            //返回错误信息（JSON）
            response.getWriter().write("{\"error\":\"未登录或token已过期\"}");
            //拦截请求
            return false;
        }

        return true;
    }
}
