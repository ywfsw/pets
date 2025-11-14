# 宠物管家 API (Pet Manager API) 🐾

## 💡 项目简介

这是一个基于 Spring Boot 的宠物健康管理后端服务，用于跟踪宠物的基本信息、健康事件（如疫苗、驱虫）以及体重变化记录，并支持健康提醒功能。

## 🛠️ 技术栈

* **后端框架:** Spring Boot 3.x (使用 Java 25)
* **ORM 框架:** MyBatis-Plus 3.x
* **数据库:** PostgreSQL (支持 Neon 平台)
* **构建工具:** Maven
* **API 规范:** RESTful API

## 🚀 本地启动

### 1. 克隆项目

```bash
git clone [你的代码仓库 URL]
cd pets
```

### 2. 配置数据库

确保 PostgreSQL 数据库可用，并在 `src/main/resources/application.properties` 文件中配置正确的数据库连接信息。

### 3. 构建项目

```bash
mvn clean install
```

### 4. 运行应用

在你的 IDE (IntelliJ IDEA / Eclipse) 中，找到 PetsApplication.java，右键点击并"运行"。

或者使用 Maven 命令行：

```bash
mvn spring-boot:run
```

应用启动后，默认会在 http://localhost:8080 上运行。

## 📦 API 接口文档

### 0. 健康检查接口

| 方法 | 路径 | 描述 | 请求体 (JSON) | 成功响应 (200 OK) |
|------|------|------|---------------|------------------|
| GET | `/ping` | 健康检查接口 | N/A | `"pong"` |

### 1. 点赞功能接口

| 方法 | 路径 | 描述 | 请求体 (JSON) | 成功响应 (200 OK) |
|------|------|------|---------------|------------------|
| POST | `/api/pets/{petId}/like` | 点赞一个宠物 | N/A | `{"success": true, "message": "点赞成功"}` |
| GET | `/api/pets/{petId}/likes/count` | 获取宠物的总点赞数 | N/A | `{"petId": 1, "count": 10}` |

### 2. 宠物管理接口

| 方法 | 路径 | 描述 | 请求体 (JSON) | 成功响应 (200 OK) |
|------|------|------|---------------|------------------|
| POST | `/api/pets` | 创建新宠物 | `{"speciesId": 1, "breedId": 2, "name": "咪咪", "birthday": "2020-01-01"}` | `"宠物添加成功，ID：1"` |
| GET | `/api/pets` | 获取所有宠物列表 | N/A | `[{"id": 1, "speciesId": 1, "name": "咪咪", ...}]` |
| GET | `/api/pets/page` | 分页获取宠物列表 | N/A (查询参数: pageNum=1&pageSize=10) | `{"records": [...], "total": 100, "size": 10, "current": 1, ...}` |
| GET | `/api/pets/{id}` | 根据ID获取宠物基本信息 | N/A | `{"id": 1, "speciesId": 1, "name": "咪咪", ...}` |
| GET | `/api/pets/detail/{id}` | 根据ID获取宠物详细信息(包含体重和健康事件) | N/A | `{"id": 1, "speciesLabel": "猫", "breedLabel": "英短", "name": "咪咪", "weightLogs": [...], "healthEvents": [...]}` |
| PUT | `/api/pets/{id}` | 根据ID更新宠物信息 | `{"speciesId": 1, "breedId": 3, "name": "咪咪", "birthday": "2020-01-01"}` | `{"id": 1, "speciesId": 1, "name": "咪咪", ...}` |
| DELETE | `/api/pets/{id}` | 根据ID删除宠物 | N/A | 204 No Content |
| GET | `/api/pets/species/{species}` | 根据物种查询宠物 | N/A | `[{"id": 1, "speciesId": 1, "name": "咪咪", ...}]` |

### 3. 健康事件接口

| 方法 | 路径 | 描述 | 请求体 (JSON) | 成功响应 (200 OK) |
|------|------|------|---------------|------------------|
| POST | `/api/health-events` | 创建健康事件 | `{"petId": 1, "eventTypeId": 1, "eventDate": "2023-10-01", "nextDueDate": "2024-04-01", "notes": "狂犬疫苗"}` | `"健康事件添加成功，ID：1"` |
| GET | `/api/health-events` | 获取所有健康事件 | N/A | `[{"id": 1, "petId": 1, "eventTypeId": 1, ...}]` |
| GET | `/api/health-events/page` | 分页获取健康事件 | N/A (查询参数: pageNum=1&pageSize=10) | `{"records": [...], "total": 50, "size": 10, "current": 1, ...}` |
| GET | `/api/health-events/{id}` | 根据ID获取健康事件 | N/A | `{"id": 1, "petId": 1, "eventTypeId": 1, ...}` |
| PUT | `/api/health-events/{id}` | 根据ID更新健康事件 | `{"petId": 1, "eventTypeId": 1, "eventDate": "2023-10-01", "nextDueDate": "2024-05-01", "notes": "更新的疫苗信息"}` | `{"id": 1, "petId": 1, "eventTypeId": 1, ...}` |
| DELETE | `/api/health-events/{id}` | 根据ID删除健康事件 | N/A | 204 No Content |
| GET | `/api/health-events/pet/{petId}` | 根据宠物ID获取健康事件 | N/A | `[{"id": 1, "petId": 1, "eventTypeId": 1, ...}]` |
| GET | `/api/health-events/upcoming` | 获取即将到期的健康事件（7天内） | N/A | `[{"id": 1, "petId": 1, "eventTypeId": 1, "nextDueDate": "2023-11-20", ...}]` |

### 4. 体重记录接口

| 方法 | 路径 | 描述 | 请求体 (JSON) | 成功响应 (200 OK) |
|------|------|------|---------------|------------------|
| POST | `/api/weight-logs` | 添加体重记录 | `{"petId": 1, "weightKg": 5.20, "logDate": "2023-11-13", "notes": "正常体重范围"}` | `"体重记录添加成功，ID：1"` |
| GET | `/api/weight-logs` | 获取所有体重记录 | N/A | `[{"id": 1, "petId": 1, "weightKg": 5.20, ...}]` |
| GET | `/api/weight-logs/page` | 分页获取体重记录 | N/A (查询参数: pageNum=1&pageSize=10) | `{"records": [...], "total": 30, "size": 10, "current": 1, ...}` |
| GET | `/api/weight-logs/{id}` | 根据ID获取体重记录 | N/A | `{"id": 1, "petId": 1, "weightKg": 5.20, ...}` |
| PUT | `/api/weight-logs/{id}` | 根据ID更新体重记录 | `{"petId": 1, "weightKg": 5.30, "logDate": "2023-11-13", "notes": "轻微增加"}` | `{"id": 1, "petId": 1, "weightKg": 5.30, ...}` |
| DELETE | `/api/weight-logs/{id}` | 根据ID删除体重记录 | N/A | 204 No Content |
| GET | `/api/weight-logs/pet/{petId}` | 根据宠物ID获取体重记录历史 | N/A | `[{"id": 1, "petId": 1, "weightKg": 5.20, ...}]` |
| GET | `/api/weight-logs/pet/{petId}/latest` | 获取宠物最新体重记录 | N/A | `{"id": 1, "petId": 1, "weightKg": 5.20, "logDate": "2023-11-13", ...}` |

### 5. 字典项接口

| 方法 | 路径 | 描述 | 请求体 (JSON) | 成功响应 (200 OK) |
|------|------|------|---------------|------------------|
| POST | `/api/dictItems` | 添加字典项 | `{"dictCode": "pet_species", "itemValue": "cat", "itemLabel": "猫", "sortOrder": 1}` | `"字典项添加成功，ID：1"` |
| GET | `/api/dictItems` | 获取所有字典项 | N/A | `[{"id": 1, "dictCode": "pet_species", "itemLabel": "猫", ...}]` |
| GET | `/api/dictItems/page` | 分页获取字典项 | N/A (查询参数: pageNum=1&pageSize=10) | `{"records": [...], "total": 20, "size": 10, "current": 1, ...}` |
| GET | `/api/dictItems/{id}` | 根据ID获取字典项 | N/A | `{"id": 1, "dictCode": "pet_species", "itemLabel": "猫", ...}` |
| PUT | `/api/dictItems/{id}` | 根据ID更新字典项 | `{"dictCode": "pet_species", "itemValue": "cat", "itemLabel": "猫咪", "sortOrder": 1}` | `"字典项更新成功，ID：1"` |
| DELETE | `/api/dictItems/{id}` | 根据ID删除字典项 | N/A | `"字典项删除成功，ID：1"` |
| GET | `/api/dictItems/code/{dictCode}` | 根据字典编码获取字典项列表 | N/A | `[{"id": 1, "dictCode": "pet_species", "itemLabel": "猫", ...}]` |
| GET | `/api/dictItems/lookup` | 查找接口(用于下拉选择器) | N/A (查询参数: dictCode=PET_SPECIES) | `[{"id": 1, "label": "猫", ...}]` |

### 6. 字典类型接口

| 方法 | 路径 | 描述 | 请求体 (JSON) | 成功响应 (200 OK) |
|------|------|------|---------------|------------------|
| POST | `/api/dictTypes` | 添加字典类型 | `{"dictCode": "PET_SPECIES", "dictName": "宠物物种", "notes": "用于定义宠物的物种分类", "parentCode": "PET_BASIC_INFO"}` | `"字典类型添加成功，编码：PET_SPECIES"` |
| GET | `/api/dictTypes` | 获取所有字典类型列表 | N/A | `[{"dictCode": "PET_SPECIES", "dictName": "宠物物种", "parentCode": "PET_BASIC_INFO", ...}]` |
| GET | `/api/dictTypes/page` | 分页获取字典类型 | N/A (查询参数: pageNum=1&pageSize=10) | `{"records": [...], "total": 15, "size": 10, "current": 1, ...}` |
| GET | `/api/dictTypes/{dictCode}` | 根据字典编码获取字典类型 | N/A | `{"dictCode": "PET_SPECIES", "dictName": "宠物物种", "parentCode": "PET_BASIC_INFO", ...}` |
| GET | `/api/dictTypes/parent/{parentCode}` | 根据父级编码获取字典类型列表 | N/A | `[{"dictCode": "PET_SPECIES", "dictName": "宠物物种", "parentCode": "PET_BASIC_INFO", ...}]` |
| PUT | `/api/dictTypes/{dictCode}` | 更新字典类型 | `{"dictName": "宠物物种分类", "notes": "更新的备注信息", "parentCode": "PET_BASIC_INFO"}` | `"字典类型更新成功，编码：PET_SPECIES"` |
| DELETE | `/api/dictTypes/{dictCode}` | 删除字典类型 | N/A | `"字典类型删除成功，编码：PET_SPECIES"` |

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