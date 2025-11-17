# 宠物管家 API (Pet Manager API) 🐾

## 💡 项目简介

这是一个基于 Spring Boot 的宠物健康管理后端服务，用于跟踪宠物的基本信息、健康事件（如疫苗、驱虫）以及体重变化记录，并支持健康提醒功能。

## 📚 目录

- [🛠️ 技术栈](#️-技术栈)
- [🏛️ 架构简介](#️-架构简介)
- [⚡️ 性能优化](#️-性能优化)
- [🚀 本地启动](#-本地启动)
- [📦 API 接口文档](#-api-接口文档)

## 🛠️ 技术栈

* **后端框架:** Spring Boot 3.x (使用 Java 25)
* **ORM 框架:** MyBatis-Plus 3.x
* **数据库:** PostgreSQL (支持 Neon 平台)
* **缓存:** Redis
* **构建工具:** Maven
* **API 规范:** RESTful API

## 🏛️ 架构简介

项目采用经典的三层架构模式：

- **Controller 层**: 负责接收 HTTP 请求，验证输入，并调用 Service 层处理业务逻辑。
- **Service 层**: 核心业务逻辑层。大部分业务逻辑、数据整合和缓存处理都在这一层完成。
- **Mapper/DAO 层**: 数据访问层，使用 MyBatis-Plus 与数据库进行交互。

## ⚡️ 性能优化

为了提升高频读取接口的性能，项目集成了 Redis 缓存。

- **缓存策略**: 使用 Spring Cache 注解 (`@Cacheable`, `@CacheEvict`)，将热点数据缓存到 Redis 中。
- **序列化方式**: 缓存采用 **JSON 格式**进行序列化 (`GenericJackson2JsonRedisSerializer`)，这提供了良好的灵活性和可读性，避免了 Java 默认序列化带来的版本和类型问题。
- **缓存失效**: 当对数据进行增、删、改操作时，相关的缓存会自动失效（Evict），以保证数据的一致性。
- **缓存范围**: 目前已对宠物信息、字典、健康事件、体重记录、相册等模块的查询接口实现了缓存。

## 🚀 本地启动

### 1. 克隆项目

```bash
git clone [你的代码仓库 URL]
cd pets
```

### 2. 配置环境

- **数据库**: 确保 PostgreSQL 数据库可用。
- **Redis**: 确保 Redis 服务可用。
- 在 `src/main/resources/application.properties` 文件中配置正确的数据库和 Redis 连接信息。

### 3. 构建项目

```bash
mvn clean install
```

### 4. 运行应用

在你的 IDE (IntelliJ IDEA / Eclipse) 中，找到 `PetsApplication.java`，右键点击并"运行"。

或者使用 Maven 命令行：

```bash
mvn spring-boot:run
```

应用启动后，默认会在 `http://localhost:8080` 上运行。

## 📦 API 接口文档

<details>
<summary><strong>0. 健康检查接口</strong></summary>

| 方法 | 路径 | 描述 | 成功响应 (200 OK) |
|------|------|------|------------------|
| GET | `/ping` | 健康检查 / 保活接口 | `"pong"` |

</details>

<details>
<summary><strong>1. 媒体接口 (Media API)</strong></summary>

| 方法 | 路径 | 描述 | 请求体 / 查询参数 | 成功响应 (200 OK) |
|------|------|------|-----------------|------------------|
| POST | `/api/media/upload-signature` | 获取 Cloudinary 安全上传签名 | `{"param1": "value1", "param2": "value2"}` | `{"signature": "...", "timestamp": "...", "api_key": "...", "cloud_name": "..."}` |

</details>

<details>
<summary><strong>2. 点赞功能接口</strong></summary>

| 方法 | 路径 | 描述 | 查询参数 | 成功响应 (200 OK) |
|------|------|------|----------|------------------|
| POST | `/api/pets/{petId}/like` | 点赞一个宠物 | N/A | `{"success": true, "message": "点赞成功"}` |
| GET | `/api/pets/{petId}/likes/count` | 获取宠物的总点赞数 | N/A | `{"petId": 1, "count": 10}` |
| GET | `/api/pets/leaderboard` | 获取宠物点赞排行榜 | `topN=10` | `[{"rank": 1, "petId": 12, "name": "豆豆", "likeCount": 158}, ...]` |

</details>

<details>
<summary><strong>3. 宠物管理接口</strong></summary>

| 方法 | 路径 | 描述 | 请求体 / 查询参数 | 成功响应 (200 OK) |
|------|------|------|-----------------|------------------|
| POST | `/api/pets` | 创建新宠物 (支持上传头像) | `{"speciesId": 1, "name": "咪咪", "avatarUrl": "..."}` | `"宠物添加成功，ID：1"` |
| GET | `/api/pets` | 获取所有宠物列表 | N/A | `[{"id": 1, "name": "咪咪", ...}]` |
| GET | `/api/pets/page` | 分页获取宠物列表 | `pageNum=1&pageSize=10` | `{"records": [...], "total": 100, ...}` |
| GET | `/api/pets/{id}` | 根据ID获取宠物基本信息 | N/A | `{"id": 1, "name": "咪咪", ...}` |
| GET | `/api/pets/detail/{id}` | 根据ID获取宠物详细信息 | N/A | `{"id": 1, "name": "咪咪", "weightLogs": [...], ...}` |
| PUT | `/api/pets/{id}` | 根据ID更新宠物信息 | `{"name": "咪咪", "avatarUrl": "..."}` | `{"id": 1, "name": "咪咪", ...}` |
| DELETE | `/api/pets/{id}` | 根据ID删除宠物 | N/A | 204 No Content |
| GET | `/api/pets/species/{species}` | 根据物种查询宠物 | N/A | `[{"id": 1, "name": "咪咪", ...}]` |

</details>

<details>
<summary><strong>4. 健康事件接口</strong></summary>

| 方法 | 路径 | 描述 | 请求体 / 查询参数 | 成功响应 (200 OK) |
|------|------|------|-----------------|------------------|
| POST | `/api/health-events` | 创建健康事件 | `{"petId": 1, "eventTypeId": 1, "eventDate": "2023-10-01"}` | `"健康事件添加成功，ID：1"` |
| GET | `/api/health-events` | 获取所有健康事件 | N/A | `[{"id": 1, "petId": 1, ...}]` |
| GET | `/api/health-events/page` | 分页获取健康事件 | `pageNum=1&pageSize=10` | `{"records": [...], "total": 50, ...}` |
| GET | `/api/health-events/{id}` | 根据ID获取健康事件 | N/A | `{"id": 1, "petId": 1, ...}` |
| PUT | `/api/health-events/{id}` | 根据ID更新健康事件 | `{"notes": "更新的疫苗信息"}` | `{"id": 1, "notes": "...", ...}` |
| DELETE | `/api/health-events/{id}` | 根据ID删除健康事件 | N/A | 204 No Content |
| GET | `/api/health-events/pet/{petId}` | 根据宠物ID获取健康事件 | N/A | `[{"id": 1, "petId": 1, ...}]` |
| GET | `/api/health-events/upcoming` | 获取即将到期的健康事件（7天内） | N/A | `[{"id": 1, "nextDueDate": "...", ...}]` |

</details>

<details>
<summary><strong>5. 体重记录接口</strong></summary>

| 方法 | 路径 | 描述 | 请求体 / 查询参数 | 成功响应 (200 OK) |
|------|------|------|-----------------|------------------|
| POST | `/api/weight-logs` | 添加体重记录 | `{"petId": 1, "weightKg": 5.20, "logDate": "2023-11-13"}` | `"体重记录添加成功，ID：1"` |
| GET | `/api/weight-logs` | 获取所有体重记录 | N/A | `[{"id": 1, "petId": 1, ...}]` |
| GET | `/api/weight-logs/page` | 分页获取体重记录 | `pageNum=1&pageSize=10` | `{"records": [...], "total": 30, ...}` |
| GET | `/api/weight-logs/{id}` | 根据ID获取体重记录 | N/A | `{"id": 1, "petId": 1, ...}` |
| PUT | `/api/weight-logs/{id}` | 根据ID更新体重记录 | `{"weightKg": 5.30}` | `{"id": 1, "weightKg": 5.30, ...}` |
| DELETE | `/api/weight-logs/{id}` | 根据ID删除体重记录 | N/A | 204 No Content |
| GET | `/api/weight-logs/pet/{petId}` | 根据宠物ID获取体重记录历史 | N/A | `[{"id": 1, "petId": 1, ...}]` |
| GET | `/api/weight-logs/pet/{petId}/latest` | 获取宠物最新体重记录 | N/A | `{"id": 1, "logDate": "...", ...}` |

</details>

<details>
<summary><strong>6. 字典项接口</strong></summary>

| 方法 | 路径 | 描述 | 请求体 / 查询参数 | 成功响应 (200 OK) |
|------|------|------|-----------------|------------------|
| POST | `/api/dictItems` | 添加字典项 | `{"dictCode": "pet_species", "itemLabel": "猫"}` | `"字典项添加成功，ID：1"` |
| GET | `/api/dictItems` | 获取所有字典项 | N/A | `[{"id": 1, "itemLabel": "猫", ...}]` |
| GET | `/api/dictItems/page` | 分页获取字典项 | `pageNum=1&pageSize=10` | `{"records": [...], "total": 20, ...}` |
| GET | `/api/dictItems/{id}` | 根据ID获取字典项 | N/A | `{"id": 1, "itemLabel": "猫", ...}` |
| PUT | `/api/dictItems/{id}` | 根据ID更新字典项 | `{"itemLabel": "猫咪"}` | `"字典项更新成功，ID：1"` |
| DELETE | `/api/dictItems/{id}` | 根据ID删除字典项 | N/A | `"字典项删除成功，ID：1"` |
| GET | `/api/dictItems/code/{dictCode}` | 根据字典编码获取字典项列表 | N/A | `[{"id": 1, "itemLabel": "猫", ...}]` |
| GET | `/api/dictItems/lookup` | 查找接口(用于下拉选择器) | `dictCode=PET_SPECIES` | `[{"id": 1, "label": "猫", ...}]` |

</details>

<details>
<summary><strong>7. 字典类型接口</strong></summary>

| 方法 | 路径 | 描述 | 请求体 / 查询参数 | 成功响应 (200 OK) |
|------|------|------|-----------------|------------------|
| POST | `/api/dictTypes` | 添加字典类型 | `{"dictCode": "PET_SPECIES", "dictName": "宠物物种"}` | `"字典类型添加成功，编码：PET_SPECIES"` |
| GET | `/api/dictTypes` | 获取所有字典类型列表 | N/A | `[{"dictCode": "PET_SPECIES", ...}]` |
| GET | `/api/dictTypes/page` | 分页获取字典类型 | `pageNum=1&pageSize=10` | `{"records": [...], "total": 15, ...}` |
| GET | `/api/dictTypes/{dictCode}` | 根据字典编码获取字典类型 | N/A | `{"dictCode": "PET_SPECIES", ...}` |
| GET | `/api/dictTypes/parent/{parentCode}` | 根据父级编码获取字典类型列表 | N/A | `[{"dictCode": "PET_SPECIES", ...}]` |
| PUT | `/api/dictTypes/{dictCode}` | 更新字典类型 | `{"dictName": "宠物物种分类"}` | `"字典类型更新成功，编码：PET_SPECIES"` |
| DELETE | `/api/dictTypes/{dictCode}` | 删除字典类型 | N/A | `"字典类型删除成功，编码：PET_SPECIES"` |

</details>

<details>
<summary><strong>8. 宠物相册接口</strong></summary>

| 方法 | 路径 | 描述 | 请求体 / 查询参数 | 成功响应 (200 OK) |
|------|------|------|-----------------|------------------|
| POST | `/api/petGallery` | 添加宠物相册图片 | `{"petId": 1, "imageUrl": "...", "caption": "..."}` | `"相册图片添加成功，ID：1"` |
| GET | `/api/petGallery` | 获取所有宠物相册图片 | N/A | `[{"id": 1, "petId": 1, ...}]` |
| GET | `/api/petGallery/{id}` | 根据ID获取宠物相册图片 | N/A | `{"id": 1, "petId": 1, ...}` |
| PUT | `/api/petGallery/{id}` | 根据ID更新宠物相册图片 | `{"caption": "更新后的描述"}` | `"相册图片更新成功，ID：1"` |
| DELETE | `/api/petGallery/{id}` | 根据ID删除宠物相册图片 | N/A | `"相册图片删除成功，ID：1"` |
| GET | `/api/petGallery/pet/{petId}` | 根据宠物ID获取相册列表 | N/A | `[{"id": 1, "petId": 1, ...}]` |

</details>

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