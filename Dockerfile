# --- 第 1 阶段: 构建 ---
# 使用一个官方的 Maven/Java 25 镜像来构建应用
FROM maven:3.9-eclipse-temurin-25 AS build

# 设置工作目录
WORKDIR /app

# 复制 pom.xml 并提前下载依赖 (利用 Docker 缓存)
COPY pom.xml .
RUN mvn dependency:go-offline

# 复制源代码并执行打包
COPY src ./src
RUN mvn clean package -DskipTests

# --- 第 2 阶段: 运行 ---
# 使用一个轻量级的 JRE (Java 运行环境) 镜像
FROM eclipse-temurin:25-jre-jammy

# 设置工作目录
WORKDIR /app

# 从 'build' 阶段复制打包好的 .jar 文件
# ❗ 确保这个 .jar 名字和你 pom.xml 里的一致
COPY --from=build /app/target/pets-0.0.1-SNAPSHOT.jar app.jar

# 暴露 Spring Boot 默认的 8080 端口
EXPOSE 8080

# 启动应用的命令
ENTRYPOINT ["java", "-jar", "app.jar"]