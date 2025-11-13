用户99286001976
生成一份接口文档,更新在README.md中​
TRAE AI
思考过程
用户要求生成一份接口文档并更新在README.md中。我需要：

首先搜索代码库，了解项目结构和API接口
查看现有的README.md文件
分析项目中的API相关代码
生成接口文档并更新README.md
让我先使用search_codebase工具来搜索API相关的信息。

我需要先搜索代码库，了解项目的API接口结构，然后查看现有的README.md文件，最后生成接口文档。

.java
DictItemsController.java
1-4
.md
README.md
12-19
.java
PetsController.java
1-14
.md
README.md
184-187
.md
README.md
176-183
.xml
pom.xml
1-51
.md
README.md
8-11
.java
HealthEventsController.java
1-14
.xml
pom.xml
51-102
.md
README.md
188-190
.md
README.md
124-127
.java
WeightLogController.java
1-14
.md
README.md
101-113
.xml
pom.xml
102-111
.md
README.md
115-125
.java
PetsApplicationTests.java
1-4
.md
README.md
2-7
.md
README.md
20-29
.java
IDictItemsService.java
1-4
.md
README.md
137-140
.java
PetsApplication.java
1-4
.md
README.md
90-100
.java
DictItemsServiceImpl.java
1-7
.java
DictItemsMapper.java
1-5
.properties
application.properties
1-15
.java
CodeGenerator.java
1-7
.md
README.md
128-138
.java
IPetsService.java
1-4
.java
IHealthEventsService.java
1-4
.java
PetsServiceImpl.java
1-7
.java
PetsMapper.java
1-5
.java
HealthEventsServiceImpl.java
1-7
.java
DictItems.java
1-11
.java
HealthEventsMapper.java
1-5
.java
WeightLogMapper.java
1-5
.md
README.md
71-89
.java
HealthEvents.java
1-13
.java
IWeightLogService.java
1-4
.md
README.md
32-70
.java
Pets.java
1-13
.java
WeightLogServiceImpl.java
1-7
.gitignore
1-50
mvnw
1-30
.md
README.md
141-152
.java
WeightLog.java
1-14
.md
README.md
154-165
.cmd
mvnw.cmd
1-30
mvnw
30-75
.xml
PetsMapper.xml
1-6
.xml
DictItemsMapper.xml
1-6
.xml
HealthEventsMapper.xml
1-6
.gitattributes
1-3
.xml
WeightLogMapper.xml
1-6
.md
README.md
166-175
mvnw
123-154
.cmd
mvnw.cmd
30-68
.cmd
mvnw.cmd
105-139
mvnw
196-222
mvnw
75-123
.cmd
mvnw.cmd
68-105
mvnw
154-196
mvnw
292-296
mvnw
222-252
.cmd
mvnw.cmd
165-190
.cmd
mvnw.cmd
139-165
mvnw
252-292
让我查看HealthEventsController和WeightLogController的完整代码，以便了解所有可用的API接口细节。

src/main/java/com/tox/tox/pets/controller/HealthEventsController.java
src/main/java/com/tox/tox/pets/controller/WeightLogController.java
让我查看DictItemsController的完整代码，以及完整的PetsController代码，以便了解所有API接口。

src/main/java/com/tox/tox/pets/controller/DictItemsController.java
src/main/java/com/tox/tox/pets/controller/PetsController.java
现在让我查看现有的README.md文件，以便更新接口文档。

README.md
我已经收集了所有的API接口信息，现在将更新README.md文件，创建一个完整的接口文档。


markdown
Apply
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

### 1. 宠物管理接口

| 方法 | 路径 | 描述 | 请求体 (JSON) | 成功响应 (200 OK) |
|------|------|------|---------------|------------------|
| POST | `/pets` | 创建新宠物 | `{"speciesId": 1, "breedId": 2, "name": "咪咪", "birthday": "2020-01-01"}` | `"宠物添加成功，ID：1"` |
| GET | `/pets` | 获取所有宠物列表 | N/A | `[{"id": 1, "speciesId": 1, "name": "咪咪", ...}]` |
| GET | `/pets/page` | 分页获取宠物列表 | N/A (查询参数: pageNum=1&pageSize=10) | `{"records": [...], "total": 100, "size": 10, "current": 1, ...}` |
| GET | `/pets/{id}` | 根据ID获取宠物基本信息 | N/A | `{"id": 1, "speciesId": 1, "name": "咪咪", ...}` |
| GET | `/pets/detail/{id}` | 根据ID获取宠物详细信息(包含体重和健康事件) | N/A | `{"id": 1, "speciesLabel": "猫", "breedLabel": "英短", "name": "咪咪", "weightLogs": [...], "healthEvents": [...]}` |
| PUT | `/pets/{id}` | 根据ID更新宠物信息 | `{"speciesId": 1, "breedId": 3, "name": "咪咪", "birthday": "2020-01-01"}` | `{"id": 1, "speciesId": 1, "name": "咪咪", ...}` |
| DELETE | `/pets/{id}` | 根据ID删除宠物 | N/A | 204 No Content |
| GET | `/pets/species/{species}` | 根据物种查询宠物 | N/A | `[{"id": 1, "speciesId": 1, "name": "咪咪", ...}]` |

### 2. 健康事件接口

| 方法 | 路径 | 描述 | 请求体 (JSON) | 成功响应 (200 OK) |
|------|------|------|---------------|------------------|
| POST | `/health-events` | 创建健康事件 | `{"petId": 1, "eventTypeId": 1, "eventDate": "2023-10-01", "nextDueDate": "2024-04-01", "notes": "狂犬疫苗"}` | `"健康事件添加成功，ID：1"` |
| GET | `/health-events` | 获取所有健康事件 | N/A | `[{"id": 1, "petId": 1, "eventTypeId": 1, ...}]` |
| GET | `/health-events/page` | 分页获取健康事件 | N/A (查询参数: pageNum=1&pageSize=10) | `{"records": [...], "total": 50, "size": 10, "current": 1, ...}` |
| GET | `/health-events/{id}` | 根据ID获取健康事件 | N/A | `{"id": 1, "petId": 1, "eventTypeId": 1, ...}` |
| PUT | `/health-events/{id}` | 根据ID更新健康事件 | `{"petId": 1, "eventTypeId": 1, "eventDate": "2023-10-01", "nextDueDate": "2024-05-01", "notes": "更新的疫苗信息"}` | `{"id": 1, "petId": 1, "eventTypeId": 1, ...}` |
| DELETE | `/health-events/{id}` | 根据ID删除健康事件 | N/A | 204 No Content |
| GET | `/health-events/pet/{petId}` | 根据宠物ID获取健康事件 | N/A | `[{"id": 1, "petId": 1, "eventTypeId": 1, ...}]` |
| GET | `/health-events/upcoming` | 获取即将到期的健康事件（7天内） | N/A | `[{"id": 1, "petId": 1, "eventTypeId": 1, "nextDueDate": "2023-11-20", ...}]` |

### 3. 体重记录接口

| 方法 | 路径 | 描述 | 请求体 (JSON) | 成功响应 (200 OK) |
|------|------|------|---------------|------------------|
| POST | `/weight-logs` | 添加体重记录 | `{"petId": 1, "weightKg": 5.20, "logDate": "2023-11-13", "notes": "正常体重范围"}` | `"体重记录添加成功，ID：1"` |
| GET | `/weight-logs` | 获取所有体重记录 | N/A | `[{"id": 1, "petId": 1, "weightKg": 5.20, ...}]` |
| GET | `/weight-logs/page` | 分页获取体重记录 | N/A (查询参数: pageNum=1&pageSize=10) | `{"records": [...], "total": 30, "size": 10, "current": 1, ...}` |
| GET | `/weight-logs/{id}` | 根据ID获取体重记录 | N/A | `{"id": 1, "petId": 1, "weightKg": 5.20, ...}` |
| PUT | `/weight-logs/{id}` | 根据ID更新体重记录 | `{"petId": 1, "weightKg": 5.30, "logDate": "2023-11-13", "notes": "轻微增加"}` | `{"id": 1, "petId": 1, "weightKg": 5.30, ...}` |
| DELETE | `/weight-logs/{id}` | 根据ID删除体重记录 | N/A | 204 No Content |
| GET | `/weight-logs/pet/{petId}` | 根据宠物ID获取体重记录历史 | N/A | `[{"id": 1, "petId": 1, "weightKg": 5.20, ...}]` |
| GET | `/weight-logs/pet/{petId}/latest` | 获取宠物最新体重记录 | N/A | `{"id": 1, "petId": 1, "weightKg": 5.20, "logDate": "2023-11-13", ...}` |

### 4. 字典项接口

| 方法 | 路径 | 描述 | 请求体 (JSON) | 成功响应 (200 OK) |
|------|------|------|---------------|------------------|
| POST | `/dictItems` | 添加字典项 | `{"dictCode": "pet_species", "itemValue": "cat", "itemLabel": "猫", "sortOrder": 1}` | `"字典项添加成功，ID：1"` |
| GET | `/dictItems` | 获取所有字典项 | N/A | `[{"id": 1, "dictCode": "pet_species", "itemLabel": "猫", ...}]` |
| GET | `/dictItems/page` | 分页获取字典项 | N/A (查询参数: pageNum=1&pageSize=10) | `{"records": [...], "total": 20, "size": 10, "current": 1, ...}` |
| GET | `/dictItems/{id}` | 根据ID获取字典项 | N/A | `{"id": 1, "dictCode": "pet_species", "itemLabel": "猫", ...}` |
| PUT | `/dictItems/{id}` | 根据ID更新字典项 | `{"dictCode": "pet_species", "itemValue": "cat", "itemLabel": "猫咪", "sortOrder": 1}` | `"字典项更新成功，ID：1"` |
| DELETE | `/dictItems/{id}` | 根据ID删除字典项 | N/A | `"字典项删除成功，ID：1"` |
| GET | `/dictItems/code/{dictCode}` | 根据字典编码获取字典项列表 | N/A | `[{"id": 1, "dictCode": "pet_species", "itemLabel": "猫", ...}]` |

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