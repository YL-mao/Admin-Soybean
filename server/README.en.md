# YLmao-Soybean · Backend (server)

Spring Boot admin API. Full docs:

- Chinese: [README.md](../README.md)
- English: [README.en.md](../README.en.md)

## This directory

| Item | Notes |
| --- | --- |
| Entry | `com.ylmao.admin.AdminApp` |
| Dev port | **8085** (`application-dev.yml`) |
| Prod port | **8081** (`application-prod.yml`) |
| Seed SQL | [`doc/admin_soybean.sql`](doc/admin_soybean.sql) |
| Nginx sample | [`doc/nginx-admin.example.conf`](doc/nginx-admin.example.conf) |
| License | Root [`LICENSE`](../LICENSE) (MIT · YLmao) |

```bash
./mvnw spring-boot:run
```

Conventions: [`AGENTS.md`](../AGENTS.md).
