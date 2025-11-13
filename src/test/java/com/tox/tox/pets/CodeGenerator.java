package com.tox.tox.pets;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import java.util.Arrays;
import java.util.List;

/**
 * MyBatis-Plus 代码生成器
 * * @author tox (根据你的 pom.xml 自动设置)
 */
public class CodeGenerator {

    /**
     * ！！！注意！！！
     * 1. 运行前, 请确保 application.properties 已被 Maven 加载
     * 2. ！！！在这里填入你【重置后】的【新密码】！！！
     */
    private static final String DB_PASSWORD = "npg_P3kxqru9vGDV";

    // 数据库连接信息 (从你的 properties 里复制)
    private static final String DB_URL = "jdbc:postgresql://ep-hidden-frog-a1yb23wc-pooler.ap-southeast-1.aws.neon.tech:5432/neondb";
    private static final String DB_USERNAME = "neondb_owner";

    // 要生成的表名 (我们之前创建的)
    private static final List<String> TABLES_TO_GENERATE = Arrays.asList("dict_types");

    // 基础包名 (根据你的 pom.xml)
    private static final String BASE_PACKAGE = "com.tox.tox.pets";

    
    /**
     * 运行这个 main 方法来生成代码
     */
    public static void main(String[] args) {
        
        // 1. 配置数据源
        DataSourceConfig.Builder dscBuilder = new DataSourceConfig.Builder(
                DB_URL, 
                DB_USERNAME, 
                DB_PASSWORD // ❗ 使用你在这里填的密码
        );
        
        // 2. 执行生成器
        FastAutoGenerator.create(dscBuilder)
                // 3. 全局配置
                .globalConfig(builder -> {
                    builder.author("tox") // 设置作者
                            .outputDir(System.getProperty("user.dir") + "/src/main/java") // 文件输出目录
                            .commentDate("yyyy-MM-dd"); // 日期格式
                })
                // 4. 包配置
                .packageConfig(builder -> {
                    builder.parent(BASE_PACKAGE) // 父包名
                            .entity("model")      // 实体类包
                            .mapper("mapper")     // Mapper 接口包
                            .service("service")   // Service 接口包
                            .serviceImpl("service.impl") // Service 实现包
                            .controller("controller");  // Controller 包
                })
                // 5. 策略配置 (最重要)
                .strategyConfig(builder -> {
                    builder.addInclude(TABLES_TO_GENERATE) // ❗ 设置需要生成的表名
                            
                            // --- Entity 策略 ---
                            .entityBuilder()
                            .enableLombok() // ❗ 开启 Lombok
                            .naming(NamingStrategy.underline_to_camel) // 表名转驼峰
                            .columnNaming(NamingStrategy.underline_to_camel) // 字段转驼峰
                            .enableFileOverride()
                            
                            // --- Mapper 策略 ---
                            .mapperBuilder()
                            .enableMapperAnnotation() // ❗ 在 Mapper 接口上添加 @Mapper 注解
                            .enableFileOverride()
                            
                            // --- Controller 策略 ---
                            .controllerBuilder()
                            .enableFileOverride()
                            .enableRestStyle(); // ❗ 开启 @RestController 风格
                })
                .execute(); // 执行

        System.out.println("✅ 代码生成完毕！");
        System.out.println("✅ 请刷新你的项目目录 (Refresh) 查看新文件。");
    }
}