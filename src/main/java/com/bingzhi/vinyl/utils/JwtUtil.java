package com.bingzhi.vinyl.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Component
public class JwtUtil {

    //签名秘钥：相当于私钥，只有服务器知道
    //实际项目放在配置文件里
    private static final String SECRET = "bingzhi_vinyl_secret_key_2026_very_long_32bytes";

    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    //过期时间：七天（单位：毫秒）
    private static final long EXPIRATION=7*24*60*60*1000;

    //生成token
    public static String generateToken(Long userId,String username,String role){
        //创建payload(载荷)，存放用户信息
        Map<String,Object> claims=new HashMap<>();
        claims.put("userId",userId);
        claims.put("username",username);
        claims.put("role",role);

        //生成token  Jwt s是一个工具类
        return Jwts.builder()
                .setClaims(claims)  //放入用户信息
                .setIssuedAt(new Date())  //设置签发时间
                //设置过期时间
                .setExpiration(new Date(System.currentTimeMillis()+EXPIRATION))
                //用h5256算法和秘钥签名
                .signWith(SECRET_KEY)
                .compact();  //压缩成字符串
    }

    //解析token
    //Claims是JWT框架的接口，用法上等同于JWt载荷数据的容器
    public static Claims parseToken(String token){
        try{
            //parser():创建一个JWT解析器构建器
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)    //用秘钥验证签名
                    .build()
                    .parseClaimsJws(token)  //解析token
                    .getBody();  //获取payload
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    //从token获取用户信息
    public static Long getUserId(String token){
        try {
            Claims claims = parseToken(token); // 解析token

            // 关键：先判断 claims 是不是 null
            if (claims == null) {
                return null; // 无效token直接返回null
            }

            return claims.get("userId", Long.class);
        } catch (Exception e) {
            // 任何异常都返回null，绝不崩溃
            return null;
        }
    }

    public static String getUsername(String token){
        Claims claims=parseToken(token);
        return claims.get("username",String.class);
    }

    public static String getRole(String token){
        Claims claims=parseToken(token);
        return claims.get("role",String.class);
    }

    //验证token是否有效
    public static boolean validateToken(String token){
        try{
            parseToken(token); //能解析就有效
            return true;
        }catch (Exception e){
            //解析失败
            System.out.println("验证失败: " + e.getMessage());
            e.printStackTrace();
            return  false;
        }
    }

}














