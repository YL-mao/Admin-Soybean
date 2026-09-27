<div align="center">
  <img src="./docs/brand/logo.png" alt="YLmao · 月亮喵" width="220" />
  <h1>Admin-Soybean</h1>
  <p>前后端分离的中后台脚手架</p>
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

用于继续做业务管理系统的 **开源脚手架**，不是某一套最终生产业务系统本身。

| 端 | 技术 | 目录 |
| --- | --- | --- |
| 前端 | [SoybeanAdmin](https://github.com/soybeanjs/soybean-admin) · Vue 3 · Vite · TypeScript · Naive UI · UnoCSS | [`web/`](web/) |
| 后端 | Spring Boot 4 · Java 25 · Sa-Token · MyBatis-Plus · MySQL · Redis | [`server/`](server/) |

## 简介

提供登录鉴权、RBAC、组织人事、系统配置、字典、公告、文件、日志、在线用户、访问控制、定时任务等常见后台能力；种子数据与权限菜单已对齐，可直接作为业务系统起点扩展。

- 后端包名：`com.ylmao.admin`，启动类 `AdminApp`
- 前端按 Soybean 官方规范实现页面与请求封装
- 协作约定见 [`AGENTS.md`](AGENTS.md)（URL / DTO / 数据库 / 权限种子等）

## 功能一览

| 模块 | 说明 |
| --- | --- |
| 认证 | 登录 / 注销、图形验证码、失败锁定与 IP 限流（`security.*`） |
| 会话安全 | 管理端单登录（device=`admin`）、会话指纹可配（IP / UA / DeviceId） |
| 权限 | 菜单 + 按钮权限码、角色授权、动态路由 |
| 组织人事 | 用户、角色、部门、岗位 |
| 系统设置 | 配置分组、字典、公告（含收件箱 / 控制台）、文件与虚拟目录 |
| 安全运维 | 操作 / 登录日志、在线用户强退、访问控制（黑白名单）、定时任务 |

## 技术栈

| 类型 | 技术 |
| --- | --- |
| 前端 | Vue 3、Vite、TypeScript、Naive UI、UnoCSS、Pinia、Elegant Router |
| 后端 | Spring Boot **4.1**、Java **25** |
| 权限会话 | Sa-Token **1.45** + Redis |
| 数据访问 | MyBatis-Plus、MySQL 8 |
| 其它 | Hutool、EasyExcel、springdoc（开发环境） |

## 环境要求

- **JDK 25+**
- **Node.js 20+** / **pnpm 10+**（见 `web/package.json`）
- **MySQL 8.0+**
- **Redis 6.0+**（登录态、验证码、配置缓存、限流等，必需）
- Maven：可用项目自带 `server/mvnw` / `mvnw.cmd`

## 仓库结构

```text
.
├── AGENTS.md                 # 开发与 AI 协作约定
├── docs/brand/               # 品牌 Logo
├── LICENSE                   # MIT · 月亮喵
├── web/                      # Soybean 前端
│   ├── src/
│   └── .env / .env.test      # 前端环境与后端地址
├── server/                   # Spring Boot API
│   ├── doc/
│   │   ├── admin_soybean.sql           # 全量种子
│   │   └── nginx-admin.example.conf    # Nginx 示例
│   ├── src/main/java/com/ylmao/admin/
│   └── src/main/resources/
│       ├── application.yml
│       ├── application-dev.yml         # 开发：默认端口 8085
│       └── application-prod.yml        # 生产：默认端口 8081
└── upload/                   # 本地上传目录（运行时，勿提交业务文件）
```

## 快速开始

### 1. 克隆

```bash
# Gitee
git clone https://gitee.com/ylmao/admin-soybean.git
# GitHub
git clone https://github.com/YL-mao/admin-soybean.git
cd admin-soybean
```

### 2. 初始化数据库

创建库（名称与配置一致即可），导入种子：

```bash
mysql -u <user> -p <database> < server/doc/admin_soybean.sql
```

种子含菜单权限、`security.*` / `log.*` / 指纹开关等配置；缺项可能导致启动校验失败。

### 3. 修改后端连接

编辑（**勿把真实生产密码提交到公开仓库**）：

| 文件 | 用途 |
| --- | --- |
| `server/src/main/resources/application-dev.yml` | 开发 MySQL / Redis / 端口（默认 **8085**） |
| `server/src/main/resources/application-prod.yml` | 生产 MySQL / Redis / 端口（默认 **8081**） |

将其中的 `username` / `password` 等改成本地值。时区由 `application.yml` 的 `app.timezone`（默认 `Asia/Shanghai`）统一。

### 4. 启动后端

```bash
cd server

# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

默认 **dev** profile。接口文档（springdoc）仅开发环境开启。

### 5. 启动前端

确认 `web/.env.test`（或当前 mode）中后端地址与端口一致，例如：

```env
VITE_SERVICE_BASE_URL=http://127.0.0.1:8085
```

```bash
cd web
pnpm install
pnpm dev
```

浏览器打开终端提示的本地地址（一般为 `http://localhost:9527` 一类 Vite 端口）。开发态可走 HTTP 代理（`VITE_HTTP_PROXY=Y`）。

### 6. 默认账号

| 账号 | 密码 |
| --- | --- |
| `admin` | `admin` |

**首次登录后请立即修改密码。**

## 常用说明

### 认证与请求头

- Token 仅走请求头 **`saToken`**（不依赖 Cookie）
- 会话指纹设备标识：**`X-Device-Id`**（前端本地生成并持久化）
- 管理端登录：`POST /api/admin/auth/login`
- 验证码：`GET /api/admin/auth/captchaImage`

### 会话指纹（可选）

在线用户页 →「指纹配置」可开关：

- `security.fpCheckIp`
- `security.fpCheckUa`
- `security.fpCheckDevice`

三项全关则不做指纹比对；登录仍会写入指纹字段。

### 生产构建

```bash
# 后端
cd server
./mvnw clean package -DskipTests
java -jar target/admin-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# 前端
cd web
pnpm build
# 产物：web/dist
```

同域部署时，将 `VITE_SERVICE_BASE_URL` 配为站点根或留空（按你的构建环境），由 Nginx 分流静态与 API。示例：[`server/doc/nginx-admin.example.conf`](server/doc/nginx-admin.example.conf)（须传 `X-Real-IP`；代理非本机回环时把对端地址加入 `app.client-ip.trusted-proxies`）。

**上线前注意：** `SaTokenConfigure` 中生产 CORS 白名单当前为占位域名（`example.com`）。真正部署时必须改成你的前端站点源，否则浏览器会拦跨域请求。

### 多实例

各应用节点须连接 **同一 Redis**，在线用户、强退、验证码与限流才会一致。

## 二次开发建议

1. 新业务模块后端对照 **`user`（主）/ `role`（辅）**：Controller URL、DTO/VO、Service 校验、菜单按钮权限种子。
2. 前端严格按 [Soybean 文档](https://docs.soybeanjs.cn) 与 [代码规范](https://docs.soybeanjs.cn/zh/standard) 做页面与请求。
3. 数据库以表结构为事实来源；种子改完检查唯一约束与孤儿关联。
4. 详细约定见 [`AGENTS.md`](AGENTS.md)；接口联调见 [`server/doc/接口文档与开发说明.md`](server/doc/接口文档与开发说明.md)。

## 致谢

| 项目 | 说明 |
| --- | --- |
| [SoybeanAdmin](https://github.com/soybeanjs/soybean-admin) | 前端模板与工程规范 |
| [Sa-Token](https://sa-token.cc) | 登录鉴权与会话 |
| [Naive UI](https://www.naiveui.com) | 前端组件库 |
| [MyBatis-Plus](https://baomidou.com) | ORM 增强 |

后端业务脚手架由本仓库维护；前端在 Soybean 之上对接真实 API 并收敛权限与运维模块。

## 许可证

- 本仓库脚手架整体：[LICENSE](LICENSE)（MIT · 月亮喵 / YLmao）
- 前端基于 SoybeanAdmin 的上游许可：另见 [`web/LICENSE`](web/LICENSE)

使用与分发时请同时遵守上述许可证及所依赖开源组件的条款。

## 相关链接

- Gitee：https://gitee.com/ylmao/admin-soybean
- GitHub：https://github.com/YL-mao/admin-soybean
- Soybean 文档：https://docs.soybeanjs.cn
- 协作约定：[`AGENTS.md`](AGENTS.md)
- English：[README.md](README.md)
