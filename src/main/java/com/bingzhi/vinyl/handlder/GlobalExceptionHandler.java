package com.bingzhi.vinyl.handlder;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

//集中捕获所有restController的异常，返回标准json格式
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Map<String,Object> handleRuntimeException(RuntimeException e){
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", e.getMessage());
        result.put("success", false);
        return result;
    }
}
