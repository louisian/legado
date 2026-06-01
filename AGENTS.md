# AGENTS.md

本文件适用于 `E:\work\legado` 及其所有子目录。后续 agent 必须优先遵守本文件，其次遵守用户在当前会话中的补充要求。

## 关键规则

1. 不允许执行任何数据库写入、迁移、修改数据相关的指令。这类操作只能把要执行的命令或语句提供给用户，由用户自行执行；包括但不限于 Alembic、Room 迁移写入、会修改真实数据的测试或脚本。
2. 涉及 UI 操作时，必须使用对应 UI 库的 MCP；例如 Ant Design 使用 antd MCP，shadcn/ui 使用 shadcn MCP。若当前 UI 库没有可用 MCP，先明确说明限制，再按仓库现有组件和样式实现。
3. 前端修改后，必须运行 `pnpm run format` 和 `pnpm run verify:policy` 进行格式化和策略校验；若当前 package 未提供脚本，必须在结果中明确说明脚本缺失，不能声称已通过。
4. 修改时后端优先。排序、账号提交、数据整理、校验等前后端都可以完成的能力，优先放在 Kotlin/服务/数据层；前端只负责展示、交互触发和轻量状态。
5. 前端开发时，禁止使用任何 Card 组件，除非用户明确说明需要使用；在 `modules/web` 中尤其不要使用 `<el-card>` 或自行实现卡片化容器来规避该规则。
6. 不要擅自改 Gradle 仓库源、签名配置、包名、Firebase 配置或生成的构建产物。`settings.gradle` 中镜像仓库注释明确写着“不要提交修改”。
7. 不要回滚用户已有改动。提交、暂存或格式化前必须先检查 `git status --short`，只处理本次任务相关文件。

## 文档分工

- 项目结构、模块职责、技术栈和关键目录说明维护在 `ARCHITECTURE.md`。
- 近期 git 修改、分支上下文和提交摘要维护在 `CHANGELOG_GIT.md`。
- 本文件只维护长期 agent 操作规则、验证要求和提交规范，不放近期修改内容。

## 常用验证

- Android Debug 构建：`.\gradlew.bat assembleDebug` 或 `.\build_debug.bat`。
- Android 单元测试：优先使用相关模块的 Gradle test 任务；不要运行会写入真实数据库或依赖设备数据的测试。
- Release 构建：`.\build_release.bat`，但它依赖本地签名配置；不要替用户生成或修改签名信息，除非用户明确要求。
- Web 前端在 `modules/web` 下执行：`pnpm run format`、`pnpm run build`。按用户规则，前端改动还必须执行 `pnpm run verify:policy`，脚本不存在时要如实报告。

## 代码和实现约定

- Android/Kotlin 修改应跟随现有风格：ViewBinding、`BaseDialogFragment`、`ReadBookConfig.save()`、`postEvent(EventBus.UP_CONFIG, ...)` 等仓库已有机制优先。
- 新增用户可见文案时，同步维护 `values/strings.xml` 以及已有多语言资源文件，避免只改默认语言。
- 数据层修改必须先读现有 DAO、实体和 Room schema。涉及 schema/migration 的实际执行命令只能交给用户。
- Web 修改应使用 Element Plus 和仓库已有 Vue 组件模式；禁止引入新的 UI 框架，除非用户明确要求。
- 不要把业务排序、账号提交、数据校验下沉到前端临时处理；能在后端或 Android 数据/服务层完成的逻辑必须放在后端优先的位置。

## 提交规则

git 提交消息必须使用英文，并符合：

```text
<type>(<scope>): <subject>
```

- `scope` 选填，表示影响范围，如 `app`、`web`、`read`、`build`。
- `subject` 必填，用英文简短描述提交内容。
- `type` 必须使用以下之一：`feat`、`fix`、`docs`、`style`、`refactor`、`perf`、`test`、`chore`、`revert`、`build`。
- 示例：`feat(read): add dialogue color configuration`

提交前必须确认暂存范围只包含本次任务相关文件。
