# YLmao-Soybean · 后端（server）

本目录为 **Spring Boot 管理端 API**。完整说明见仓库根目录：

- 中文：[README.md](../README.md)
- English：[README.en.md](../README.en.md)

## 本目录要点

| 项 | 说明 |
| --- | --- |
| 启动类 | `com.ylmao.admin.AdminApp` |
| 开发端口 | `application-dev.yml` 默认 **8085** |
| 生产端口 | `application-prod.yml` 默认 **8081** |
| 种子 SQL | [`doc/admin_soybean.sql`](doc/admin_soybean.sql) |
| Nginx 示例 | [`doc/nginx-admin.example.conf`](doc/nginx-admin.example.conf) |
| 许可证 | 根目录 [`LICENSE`](../LICENSE)（MIT · 月亮喵） |

```bash
# 开发启动（在 server/ 下）
./mvnw spring-boot:run          # Linux / macOS
.\mvnw.cmd spring-boot:run      # Windows
```

开发约定见根目录 [`AGENTS.md`](../AGENTS.md)。
