package com.yellow.petshop;

import io.github.cdimascio.dotenv.Dotenv;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.yellow.petshop.mapper")
@EnableScheduling  // 启用定时任务
public class PetserverApplication {

    public static void main(String[] args) {
        // 加载 .env 文件（默认读取项目根目录的 .env）
        Dotenv dotenv = Dotenv.load();

        // 将 .env 中的配置注入到系统环境变量（关键步骤）
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
        SpringApplication.run(PetserverApplication.class, args);
    }

}
