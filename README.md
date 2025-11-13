# 宠物管家 API (Pet Manager API) 🐾

一个简单的宠物健康和体重管理后端服务。

---

## 💡 项目简介

这是一个基于 Spring Boot 的宠物健康管理后端服务，用于跟踪宠物的基本信息、健康事件（如疫苗、驱虫）以及体重变化记录，并支持健康提醒功能。

## 🛠️ 技术栈

* **后端框架:** Spring Boot 3.x (使用 Java 25)
* **ORM 框架:** MyBatis-Plus 3.x
* **数据库:** PostgreSQL (支持 Neon 平台)
* **构建工具:** Maven
* **API 规范:** RESTful API

## 📁 项目结构


## 🚀 本地启动

### 1. 克隆项目

```bash
git clone [你的代码仓库 URL]
cd pets
```

### 2. 准备数据库
本项目使用 PostgreSQL。

你需要一个数据库实例（推荐使用 Neon 的免费套餐）。

在你的数据库中，手动执行以下 DDL 脚本来创建表结构：

```sql
-- 创建宠物表
CREATE TABLE pets (
    id SERIAL PRIMARY KEY,
    species VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    breed VARCHAR(100),
    birthday DATE,
    created_at TIMESTAMP NOT NULL
);

-- 创建健康事件表
CREATE TABLE health_events (
    id SERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    event_type VARCHAR(100) NOT NULL,
    event_date DATE NOT NULL,
    next_due_date DATE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL
);

-- 创建体重记录表
CREATE TABLE weight_log (
    id SERIAL PRIMARY KEY,
    pet_id BIGINT NOT NULL REFERENCES pets(id) ON DELETE CASCADE,
    weight_kg DECIMAL(5,2) NOT NULL,
    log_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

### 3. 配置连接
在 src/main/resources/ 目录下，找到 application.properties 文件。

修改以下配置，使其指向你自己的数据库：

```properties
# =======================================================
# == 数据库连接 (DataSource) - 核心
# =======================================================
# 你的 Neon 数据库 JDBC URL (已包含 sslmode=require)
spring.datasource.url=jdbc:postgresql://YOUR_HOST:5432/YOUR_DB

# 你的数据库用户名
spring.datasource.username=YOUR_USERNAME

# 你的数据库密码
spring.datasource.password=YOUR_SECURE_PASSWORD
```

### 4. 运行应用
（推荐）在你的 IDE (IntelliJ IDEA / Eclipse) 中，找到 PetsApplication.java，右键点击并"运行"。

或者使用 Maven 命令行：

```bash
mvn spring-boot:run
```

应用启动后，默认会在 http://localhost:8080 上运行。

## 📦 API 接口

### 宠物管理接口

| 方法 | 路径 | 描述 | 请求体 (JSON) | 成功响应 (200 OK) |
|------|------|------|---------------|------------------|
| POST | `/pets` | 创建新宠物 | `{"species": "猫", "name": "咪咪", "breed": "英短", "birthday": "2020-01-01"}` | `{"id": 1, "species": "猫", "name": "咪咪", "breed": "英短", "birthday": "2020-01-01", "createdAt": "2023-11-13T10:00:00"}` |
| GET | `/pets` | 获取所有宠物列表 | N/A | `[{"id": 1, "species": "猫", "name": "咪咪", ...}]` |
| GET | `/pets/page` | 分页获取宠物列表 | N/A (查询参数: pageNum=1&pageSize=10) | `{"records": [...], "total": 100, "size": 10, "current": 1, ...}` |
| GET | `/pets/{id}` | 根据ID获取宠物信息 | N/A | `{"id": 1, "species": "猫", "name": "咪咪", ...}` |
| PUT | `/pets/{id}` | 根据ID更新宠物信息 | `{"species": "猫", "name": "咪咪", "breed": "美短", "birthday": "2020-01-01"}` | `{"id": 1, "species": "猫", "name": "咪咪", "breed": "美短", ...}` |
| DELETE | `/pets/{id}` | 根据ID删除宠物 | N/A | 204 No Content |
| GET | `/pets/species/{species}` | 根据物种查询宠物 | N/A | `[{"id": 1, "species": "猫", "name": "咪咪", ...}]` |

### 健康事件接口

| 方法 | 路径 | 描述 | 请求体 (JSON) | 成功响应 (200 OK) |
|------|------|------|---------------|------------------|
| POST | `/health-events` | 创建健康事件 | `{"petId": 1, "eventType": "疫苗", "eventDate": "2023-10-01", "nextDueDate": "2024-04-01", "notes": "狂犬疫苗"}` | `{"id": 1, "petId": 1, "eventType": "疫苗", ...}` |
| GET | `/health-events` | 获取所有健康事件 | N/A | `[{"id": 1, "petId": 1, "eventType": "疫苗", ...}]` |
| GET | `/health-events/page` | 分页获取健康事件 | N/A (查询参数: pageNum=1&pageSize=10) | `{"records": [...], "total": 50, "size": 10, "current": 1, ...}` |
| GET | `/health-events/{id}` | 根据ID获取健康事件 | N/A | `{"id": 1, "petId": 1, "eventType": "疫苗", ...}` |
| PUT | `/health-events/{id}` | 根据ID更新健康事件 | `{"petId": 1, "eventType": "疫苗", "eventDate": "2023-10-01", "nextDueDate": "2024-05-01", "notes": "更新的疫苗信息"}` | `{"id": 1, "petId": 1, "eventType": "疫苗", ...}` |
| DELETE | `/health-events/{id}` | 根据ID删除健康事件 | N/A | 204 No Content |
| GET | `/health-events/pet/{petId}` | 根据宠物ID获取健康事件 | N/A | `[{"id": 1, "petId": 1, "eventType": "疫苗", ...}]` |
| GET | `/health-events/upcoming` | 获取即将到期的健康事件（7天内） | N/A | `[{"id": 1, "petId": 1, "eventType": "疫苗", "nextDueDate": "2023-11-20", ...}]` |

### 体重记录接口

| 方法 | 路径 | 描述 | 请求体 (JSON) | 成功响应 (200 OK) |
|------|------|------|---------------|------------------|
| POST | `/weight-logs` | 添加体重记录 | `{"petId": 1, "weightKg": 5.20, "logDate": "2023-11-13"}` | `{"id": 1, "petId": 1, "weightKg": 5.20, "logDate": "2023-11-13", ...}` |
| GET | `/weight-logs` | 获取所有体重记录 | N/A | `[{"id": 1, "petId": 1, "weightKg": 5.20, ...}]` |
| GET | `/weight-logs/page` | 分页获取体重记录 | N/A (查询参数: pageNum=1&pageSize=10) | `{"records": [...], "total": 30, "size": 10, "current": 1, ...}` |
| GET | `/weight-logs/{id}` | 根据ID获取体重记录 | N/A | `{"id": 1, "petId": 1, "weightKg": 5.20, ...}` |
| PUT | `/weight-logs/{id}` | 根据ID更新体重记录 | `{"petId": 1, "weightKg": 5.30, "logDate": "2023-11-13"}` | `{"id": 1, "petId": 1, "weightKg": 5.30, ...}` |
| DELETE | `/weight-logs/{id}` | 根据ID删除体重记录 | N/A | 204 No Content |
| GET | `/weight-logs/pet/{petId}` | 根据宠物ID获取体重记录历史 | N/A | `[{"id": 1, "petId": 1, "weightKg": 5.20, ...}, ...]` |
| GET | `/weight-logs/pet/{petId}/latest` | 获取宠物最新体重记录 | N/A | `{"id": 1, "petId": 1, "weightKg": 5.20, "logDate": "2023-11-13", ...}` |

## 📊 数据模型

### 宠物 (Pets)

| 字段名 | 数据类型 | 描述 |
|--------|----------|------|
| id | BIGINT | 宠物ID (主键) |
| species | VARCHAR(100) | 物种（如猫、狗） |
| name | VARCHAR(100) | 宠物名称 |
| breed | VARCHAR(100) | 品种 |
| birthday | DATE | 生日 |
| createdAt | TIMESTAMP | 记录创建时间 |

### 健康事件 (HealthEvents)

| 字段名 | 数据类型 | 描述 |
|--------|----------|------|
| id | BIGINT | 健康事件ID (主键) |
| petId | BIGINT | 关联的宠物ID (外键) |
| eventType | VARCHAR(100) | 事件类型（如疫苗、驱虫） |
| eventDate | DATE | 事件实际发生日期 |
| nextDueDate | DATE | 下次应办日期（用于生成提醒） |
| notes | TEXT | 备注（如疫苗品牌、医院等） |
| createdAt | TIMESTAMP | 记录创建时间 |

### 体重记录 (WeightLog)

| 字段名 | 数据类型 | 描述 |
|--------|----------|------|
| id | BIGINT | 体重记录ID (主键) |
| petId | BIGINT | 关联的宠物ID (外键) |
| weightKg | DECIMAL(5,2) | 体重（单位：千克） |
| logDate | DATE | 记录日期（称重日期） |
| createdAt | TIMESTAMP | 记录创建时间 |

## 🔮 未来功能规划

- [ ] 用户认证和授权系统
- [ ] 健康提醒功能（邮件/短信通知）
- [ ] 体重变化趋势分析
- [ ] 前端页面开发
- [ ] 多用户支持

## 🤝 贡献指南

欢迎提交 Issues 和 Pull Requests 来改进这个项目！

## 📝 许可证

[MIT License](LICENSE)