package com.bingzhi.vinyl;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.bingzhi.vinyl.mapper")
public class IcePaperVinylStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(IcePaperVinylStoreApplication.class, args);
    }

}
