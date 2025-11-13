# 宠物管家 API (Pet Manager API) 🐾

一个简单的宠物健康和体重管理后端服务。

---

## 💡 项目简介

这是一个个人的 Spring Boot 实践项目，用于跟踪宠物的健康事件（如疫苗、驱虫、体重变化），并为将来的智能提醒功能打下基础。

## 🛠️ 技术栈

* **后端:** Spring Boot 3.x (使用 Java 25)
* **数据层:** MyBatis-Plus 3.x
* **数据库:** PostgreSQL (专为 Neon 平台适配)
* **构建工具:** Maven

## 🚀 本地启动

#### 1. 克隆项目

```bash
git clone [你的代码仓库 URL]
cd pets
```

#### 2. 准备数据库
本项目使用 PostgreSQL。

你需要一个数据库实例（推荐使用 Neon 的免费套餐）。

在你的数据库中，手动执行项目中的 DDL 脚本来创建 pets, weight_log, health_events 表结构。

#### 3. 配置连接
在 src/main/resources/ 目录下，找到 application.properties 文件。

修改以下配置，使其指向你自己的数据库：

Properties

# =======================================================
# == 数据库连接 (DataSource) - 核心
# =======================================================
# 你的 Neon 数据库 JDBC URL (已包含 sslmode=require)
spring.datasource.url=jdbc:postgresql://YOUR_HOST:5432/YOUR_DB

# 你的数据库用户名
spring.datasource.username=YOUR_USERNAME

# 你的数据库密码
spring.datasource.password=YOUR_SECURE_PASSWORD
#### 4. 运行应用
（推荐）在你的 IDE (IntelliJ IDEA / Eclipse) 中，找到 PetsApplication.java (或你的主启动类)，右键点击并“运行”。

或者使用 Maven 命令行：

Bash

mvn spring-boot:run
应用启动后，默认会在 http://localhost:8080 上运行。

📦 API 接口
(... 待补充 ...)

POST /api/pets - 创建一个新宠物

GET /api/pets/{id} - 获取宠物信息