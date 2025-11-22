package main.java.com.petshop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 主启动类（删除多余的 DataSourceAutoConfiguration 排除配置）
 */
@SpringBootApplication // 去掉 exclude 配置，默认启用数据源自动配置
@MapperScan("com.yellow.petshop.mapper") // 扫描 Mapper 接口（必须与你的 Mapper 包路径一致）
public class YellowPetShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(YellowPetShopApplication.class, args);
        System.out.println("=====================================");
        System.out.println("  🐾 YellowPetShop 项目启动成功！ 🐾");
        System.out.println("  访问地址：http://localhost:3000");
        System.out.println("=====================================");
    }
}