# Agent 指引

- 不要臆测用户意图；用户清楚自己要什么。
- 动机或目标不清时，先停下与用户讨论。
- 目标清楚但路径不是最短时，直接说明，给出更短做法，由用户决定。
- 出问题追根因；未经明确同意，不要打补丁、加兜底或做防御性绕过。
- 输出精简，删掉不影响决策的内容。
- 改代码时加简短注释，说明这段代码的含义。
- **不要删除已有注释**，除非对应代码已删除或注释事实错误。优先小范围改动；禁止整文件重写导致丢注释或破坏 UTF-8（尤其 Java 中文）。

## 仓库结构

- 本仓库为前后端分离 monorepo：
  - `web/`：Admin-Soybean 前端（Vue3 + Vite + TypeScript + Naive UI + UnoCSS，基于 SoybeanAdmin）
  - `server/`：Spring Boot 管理端 API（包名 `com.ylmao.admin`，入口 `AdminApp`）
- 产品对外名称：**Admin-Soybean**。当前仍是后台脚手架，不是最终生产业务系统。
- 保留有用的脚手架模块（用户/角色等）、菜单、权限与种子数据，便于继续开发和 AI 辅助改码。
- 种子 SQL 须清理明显脏数据、无意义随机串、孤儿关联。
- 生产不需要的可选菜单，默认用权限/菜单配置隐藏，不要直接删脚手架引用。
- **以数据库结构为事实来源**；后端字段与前端展示/入参应对齐库字段名、类型、默认值、注释与约束。
- 影响行为或数据契约的规范变更，逐项先与用户确认再改。
- 改后端模块前先对照同类已完成模块：以 `user` 为主、`role` 为辅（Controller URL、方法名、DTO/VO/PO、Service 校验、菜单/权限种子）。
- 前端业务页已对接真实 `/api/admin` 接口；页面与 UI **仍严格按 Soybean 官方文档与代码规范实现**，已对接模块以本仓库对应页为参照。

## 数据库风格

- 默认不加数据库外键；用唯一索引、普通索引、Service 校验与种子一致性检查。
- 为真实查询、唯一性、关系表使用建索引。
- 关系表用唯一索引防重复，例如 `(user_id, role_id)`、`(role_id, perm_id)`。
- 基线默认值优先落库：
  - `create_time` 默认 `CURRENT_TIMESTAMP`
  - `is_enabled` 默认 `0`（禁用）
  - `is_del` 默认 `0`（未删除）
- 适用处用 MyBatis-Plus 自动填充：`create_by`、`update_by`、`create_time`、`update_time`、`is_del`。
- 同业务含义字段保持名称、类型、默认、长度、库注释一致。
- 改表时连同字段注释、类型、长度、默认值、索引一起审视。
- 种子可保留有意义脚手架示例，禁止脏测试值与孤儿行。
- 非空权限码须唯一；两菜单同视图时用不同权限码，或隐藏其一。
- 权限在 `sys_menu`（`perm_code` 等），无独立 `sys_perm` 表；改接口时同步 Controller、前端调用与菜单/权限种子。
- 改种子后检查唯一约束、关系表重复、孤儿引用与脏数据残留。

## URL 风格（后端）

- 管理端 JSON 接口统一前缀 `/api/admin`；类级映射为 `/api/admin/{模块}`，如 `@RequestMapping("/api/admin/post")`。
- URL 不重复模块主语；方法级只用动作或业务含义：`/list`、`/add`、`/update`、`/delete`、`/checkName`、`/checkCode`、`/updateEnabled` 等。
- 禁止 `/api/admin/post/postList`、`/api/admin/role/checkRoleName` 这类重复主语。
- 文件预览流等非 JSON 管理接口可保持独立路径（如默认 `/upload/**`），不强制挂 `/api/admin`。

## 方法命名（后端）

- Java 方法名可以带模块主语；推荐 `主语 + 动词`，归属清晰。
- 示例：`postList`、`postInsert`、`checkPostNameUnique`、`updatePostEnabled`、`roleList`。
- 避免看不出归属的裸名：`list`、`add`、`update`、`delete`、`checkCode` 等。
- 改相关接口时一并修正错别字（如 Prem→Perm），并同步前端与种子。
- URL「不重复主语」规则不套用到 Java 方法名。

## PO / DTO / VO（后端）

- 增、改、启停不要用 PO 直接当 Controller 请求体。
- PO 对齐 `User`：`@Data`、`@NoArgsConstructor`、`@EqualsAndHashCode(callSuper = false)`，填充注解与字段顺序一致。
- 由 DTO 构建 PO 时用显式增/改构造，如 `new Post(PostDto.PostInsert dto)`。
- 写操作用 DTO；列表回 VO，不直接回 PO。
- DTO：`XxxDto.XxxList` / `XxxInsert` / `XxxUpdate` / `UpdateEnabled`；增改分 record + `@Valid`，不用共用 Save + Groups。
- VO：如 `PostVo.PostListVo`，适当时提供 `from(PO)`。

## Controller / Service（后端）

- 列表用 `PageQuery`；增 `POST /add`，改 `PUT /update`，删 `DELETE /delete`，启停 `PATCH /updateEnabled`。
- 唯一性校验用无重复主语 URL，如 `GET /checkName`。
- Controller 只收 DTO、调 Service、返回结果；不做业务快判。
- 基础校验放 DTO；唯一性、存在性、树约束、字典值等放 Service。
- 不要加只转调私有校验的薄包装方法。
- 业务失败抛 `BusinessException`，不要静默返回失败行数。
- 非法枚举/状态/类型统一用类似「参数不合法」。
- 库长限制与前端校验已足够时，后端少堆长度校验。
- 用户 `deptId`、`postId`、角色可空；可无密码创建但默认禁用，启用须已有密码。
- 分配请求中的角色/权限 ID 去重并清空值；非保护真实不变量时不做存在性检查。
- 用户姓名默认不唯一；角色名、角色编码须唯一。
- 删角色须清角色-权限，并禁止删除已分配给用户的角色。
- 操作日志是可观测副作用：写失败隔离在切面/日志服务并写应用日志，不得抛 `BusinessException` 影响主响应。

## 前端风格（Soybean / `web`）

- 页面与 UI **严格按 Soybean 官方文档与代码规范实现**，禁止凭记忆拼其它后台 UI 框架套路。
- 文档优先级：
 1. [Soybean 文档](https://docs.soybeanjs.cn)（指南、路由、请求、目录约定等）
 2. [SoybeanJS 代码规范](https://docs.soybeanjs.cn/zh/standard)
 3. 官方示例/预览与本仓库 `web/` 中符合上述文档的用法（仅作风格辅助，不作「已对接业务模块」样板）
- UI 组件以 **Naive UI** 为准；样式与原子类跟项目 **UnoCSS** 约定；状态用 Pinia；请求走项目既有 `service`/`hooks` 封装，不要另起一套调用方式。
- 路由遵循 Elegant Router / 项目文件路由约定；新增页面按文档与生成流程处理，不手写破坏自动路由约定的结构。
- 列表/表单等交互优先采用 Soybean + Naive UI 文档中的常规写法及项目已提供的 hooks/组件；不要为了「像其它后台页」而自造交互。
- 文案与菜单名优先走 i18n（`$t`），与 Soybean 项目惯例一致。
- API 调用集中在 `web/src/service/api`（或项目既定位置）；对接真实后端时，类型与请求方法与后端约定一致（POST/PUT/PATCH/DELETE），JSON 提交。
- 错误提示展示后端业务错误信息，跟现有请求封装/消息组件用法一致，不要静默吞错。
- 与后端交互的关键请求处可加简短注释（列表、唯一校验、保存、启停、删除、授权等）。
- 表单状态字段可默认禁用，但仍允许用户手动选启用；除非用户要求，不加解释默认禁用的额外文案。
- 前端编码格式校验：
  - `roleCode`、`postCode`、`dictTypeCode`：字母数字下划线连字符
  - `permCode`：另允许冒号
- 文档无法覆盖时，先与用户确认，再引入新交互或新依赖。
- 前端改完后按需跑 `pnpm typecheck` / `pnpm lint`（在 `web/` 下）；不要引入与 Soybean 规范冲突的格式化/lint 配置。

## 协作与契约

- 同一接口变更须同时考虑：`server` Controller/DTO/VO、`web` API 与页面、必要时种子/`sys_menu` 权限码。
- 包名、Maven 坐标、Redis key 前缀、库名等标识变更属规范项，改前先确认。

## 验证

- 模块改完后搜索旧 URL、重复主语 URL、废弃路由引用。
- Java 变更后在 `server/` 执行 `.\mvnw.cmd compile`（Windows）或 `./mvnw compile`。
- 前端相关变更在 `web/` 按上面命令做类型与 lint 检查。
- 结束前对改动执行 `git diff --check`。
- 种子 SQL 变更后做唯一、关联重复、孤儿引用与脏数据检查。
