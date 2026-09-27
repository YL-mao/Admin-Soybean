<div align="center">
  <img src="./docs/brand/logo.png" alt="YLmao · Moon Cat" width="220" />
  <h1>Admin-Soybean</h1>
  <p>A full-stack admin scaffold</p>
  <p>
    <a href="./README.md">English</a> · <a href="./README.zh-CN.md">中文</a>
  </p>
  <p>
    <img alt="license" src="https://img.shields.io/badge/license-MIT-green.svg" />
    <img alt="Java" src="https://img.shields.io/badge/Java-25-orange.svg" />
    <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen.svg" />
    <img alt="Vue" src="https://img.shields.io/badge/Vue-3-42b883.svg" />
  </p>
</div>

---

An **open-source admin scaffold** for building business management systems — not a final production product by itself.

| Side | Stack | Path |
| --- | --- | --- |
| Frontend | [SoybeanAdmin](https://github.com/soybeanjs/soybean-admin) · Vue 3 · Vite · TypeScript · Naive UI · UnoCSS | [`web/`](web/) |
| Backend | Spring Boot 4 · Java 25 · Sa-Token · MyBatis-Plus · MySQL · Redis | [`server/`](server/) |

## Overview

Includes auth, RBAC, org/HR, system config, dictionaries, notices, files, logs, online users, access control, and scheduled jobs. Seed data and menus are aligned so you can extend it as a real product base.

- Backend package: `com.ylmao.admin`, entry `AdminApp`
- Frontend follows Soybean docs for pages and request wrappers
- Conventions: [`AGENTS.md`](AGENTS.md) (URLs, DTO, DB, permission seeds)

## Features

| Area | Notes |
| --- | --- |
| Auth | Login / logout, captcha, lockout & IP rate limit (`security.*`) |
| Session | Single login for admin (`device=admin`), optional fingerprint (IP / UA / DeviceId) |
| Permissions | Menu + button codes, role grants, dynamic routes |
| Org / HR | Users, roles, departments, posts |
| Settings | Config groups, dicts, notices (inbox / console), files & virtual folders |
| Ops | Operate / login logs, force logout, allow/deny lists, jobs |

## Stack

| Layer | Tech |
| --- | --- |
| Frontend | Vue 3, Vite, TypeScript, Naive UI, UnoCSS, Pinia, Elegant Router |
| Backend | Spring Boot **4.1**, Java **25** |
| Auth / session | Sa-Token **1.45** + Redis |
| Data | MyBatis-Plus, MySQL 8 |
| Other | Hutool, EasyExcel, springdoc (dev) |

## Requirements

- **JDK 25+**
- **Node.js 20+** / **pnpm 10+** (see `web/package.json`)
- **MySQL 8.0+**
- **Redis 6.0+** (sessions, captcha, config cache, rate limit — required)
- Maven: use bundled `server/mvnw` / `mvnw.cmd`

## Layout

```text
.
├── AGENTS.md                 # Dev / AI conventions
├── docs/brand/               # Brand logo
├── LICENSE                   # MIT · YLmao (月亮喵)
├── web/                      # Soybean frontend
├── server/                   # Spring Boot API
│   └── doc/
│       ├── admin_soybean.sql
│       └── nginx-admin.example.conf
└── upload/                   # Runtime uploads (do not commit business files)
```

## Quick start

### 1. Clone

```bash
# GitHub
git clone https://github.com/YL-mao/admin-soybean.git
# Gitee
git clone https://gitee.com/ylmao/admin-soybean.git
cd admin-soybean
```

### 2. Database

Create a database, then import the seed:

```bash
mysql -u <user> -p <database> < server/doc/admin_soybean.sql
```

The seed includes menus and `security.*` / `log.*` / fingerprint configs; missing keys may fail startup checks.

### 3. Backend config

Edit (never commit real production secrets):

| File | Purpose |
| --- | --- |
| `server/src/main/resources/application-dev.yml` | Dev MySQL / Redis / port (**8085**) |
| `server/src/main/resources/application-prod.yml` | Prod MySQL / Redis / port (**8081**) |

Replace `username` / `password` with your local values. Timezone is unified via `app.timezone` in `application.yml` (default `Asia/Shanghai`).

### 4. Run backend

```bash
cd server
./mvnw spring-boot:run          # Linux / macOS
.\mvnw.cmd spring-boot:run      # Windows
```

Default profile is **dev**. springdoc is enabled in development only.

### 5. Run frontend

Point `web/.env.test` (or your mode) at the API, e.g.:

```env
VITE_SERVICE_BASE_URL=http://127.0.0.1:8085
```

```bash
cd web
pnpm install
pnpm dev
```

Open the local URL from the terminal (often `http://localhost:9527`). Optional HTTP proxy: `VITE_HTTP_PROXY=Y`.

### 6. Default account

| Username | Password |
| --- | --- |
| `admin` | `admin` |

**Change the password immediately after first login.**

## Notes

### Auth headers

- Token header: **`saToken`** (no Cookie dependency)
- Device id: **`X-Device-Id`**
- Login: `POST /api/admin/auth/login`
- Captcha: `GET /api/admin/auth/captchaImage`

### Fingerprint (optional)

Online users → fingerprint config:

- `security.fpCheckIp`
- `security.fpCheckUa`
- `security.fpCheckDevice`

All off = no fingerprint check; login still stores fingerprint fields.

### Production build

```bash
cd server
./mvnw clean package -DskipTests
java -jar target/admin-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

cd web
pnpm build
# output: web/dist
```

For same-origin deploy, set `VITE_SERVICE_BASE_URL` to the site root or leave empty per your env; use Nginx to split static vs API. Sample: [`server/doc/nginx-admin.example.conf`](server/doc/nginx-admin.example.conf) (forward `X-Forwarded-For`).

**Before real production:** update the prod CORS allowlist in `SaTokenConfigure` (currently placeholder `example.com`). Without your real frontend origin, browsers will block cross-origin API calls.

### Multi-instance

All app nodes must share the **same Redis** for online users, kick, captcha, and rate limits.

## Extending

1. Backend modules: follow **`user` (primary) / `role` (secondary)** for URLs, DTO/VO, Service checks, and permission seeds.
2. Frontend: [Soybean docs](https://docs.soybeanjs.cn) and [coding standard](https://docs.soybeanjs.cn/zh/standard).
3. Treat the database schema as source of truth; re-check unique constraints and orphan rows after seed changes.
4. See [`AGENTS.md`](AGENTS.md) and [`server/doc/接口文档与开发说明.md`](server/doc/接口文档与开发说明.md).

## Credits

| Project | Role |
| --- | --- |
| [SoybeanAdmin](https://github.com/soybeanjs/soybean-admin) | Frontend template & conventions |
| [Sa-Token](https://sa-token.cc) | Auth & sessions |
| [Naive UI](https://www.naiveui.com) | UI components |
| [MyBatis-Plus](https://baomidou.com) | ORM |

Backend scaffold is maintained here; the frontend is Soybean wired to a real API with permission and ops modules.

## License

- This scaffold: [LICENSE](LICENSE) (MIT · YLmao / 月亮喵)
- Upstream Soybean frontend license: [`web/LICENSE`](web/LICENSE)

Please also respect licenses of third-party dependencies.

## Links

- GitHub: https://github.com/YL-mao/admin-soybean
- Gitee: https://gitee.com/ylmao/admin-soybean
- Soybean docs: https://docs.soybeanjs.cn
- Conventions: [`AGENTS.md`](AGENTS.md)
- 中文：[README.zh-CN.md](README.zh-CN.md)
