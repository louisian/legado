# ARCHITECTURE.md

本文档记录 `E:\work\legado` 的长期项目结构、模块职责和常用入口。近期提交和分支上下文不要写在这里，应维护到 `CHANGELOG_GIT.md`。

## 总体架构

Legado 是 Android 阅读器项目，主体为 Kotlin/Android 应用，辅以规则解析模块、Rhino 脚本模块和一个独立 Web 管理端。

- 根工程：Gradle 多模块工程，`settings.gradle` 包含 `:app`、`:modules:book`、`:modules:rhino`。
- Android 应用：`app`，命名空间为 `io.legado.app`，当前 `applicationId` 为 `io.reader.cosmos`。
- 规则与内容解析：`modules/book`，被 Android 应用依赖。
- JavaScript/Rhino 支持：`modules/rhino`，被 Android 应用依赖。
- Web 前端：`modules/web`，Vue 3 + Vite + TypeScript + Element Plus。

## 根目录

- `README.md` / `English.md`：项目介绍、功能说明、社区和 API 入口。
- `settings.gradle`：Gradle 插件仓库、依赖仓库和模块声明。镜像仓库注释仅供本地排障，不要提交相关修改。
- `build.gradle`：根 Gradle 配置，定义 Android/Kotlin/KSP/Room 等插件别名和公共构建配置。
- `gradle.properties`：Gradle 和应用构建属性，可能包含签名、包名或 Firebase 相关配置，修改前必须确认需求。
- `gradlew.bat`：Windows Gradle Wrapper 入口。
- `build_debug.bat`：Debug APK 构建脚本，内部调用 `gradlew.bat assembleDebug`。
- `build_release.bat`：Release APK 构建脚本，要求本地签名配置存在，内部调用 `gradlew.bat clean assembleAppRelease`。
- `generate_keystore.bat`：本地 keystore 生成辅助脚本。不要替用户生成或改写签名配置，除非用户明确要求。
- `package.json`：根目录只提供 Commitizen 相关依赖，不代表主前端包。

## Android 应用模块：app

`app` 是主要 Android 应用模块，使用 Android Gradle Plugin、Kotlin、KSP、Room、ViewBinding 和 Material/AndroidX 生态。

### 构建配置

- `app/build.gradle`：应用模块构建文件。
  - `compileSdk = 36`，`minSdk = 21`，`targetSdk = 36`。
  - Java/Kotlin toolchain 使用 Java 17。
  - `buildFeatures` 开启 `buildConfig` 和 `viewBinding`。
  - Room schema 输出目录为 `app/schemas`。
  - `release` 构建开启 minify 和 shrinkResources。
  - `debug` 构建使用 `.debug` applicationId suffix。
- `app/download.gradle`：下载构建依赖或资源的辅助配置。
- `app/proguard-rules.pro`、`app/cronet-proguard-rules.pro`：混淆规则。
- `app/google-services.json`：Firebase/Google Services 配置，谨慎修改。

### 源码目录

主源码位于 `app/src/main/java/io/legado/app`：

- `api`：对外或内部 API 接口。
- `base`：Activity、Fragment、Dialog、Adapter 等基础类和通用基类。
- `constant`：常量、EventBus key、偏好设置 key、日志常量等。
- `data`：本地持久化数据层。
  - `dao`：Room DAO。
  - `entities`：Room 实体和业务数据对象，例如书籍、章节、书源、订阅、替换规则、搜索结果等。
- `exception`：项目内异常类型。
- `help`：横切辅助能力和配置。
  - `book`：书籍内容处理、正文加工、阅读相关辅助逻辑。
  - `config`：阅读配置、用户配置和偏好封装。
  - `coroutine`：协程辅助。
  - `crypto`：加密相关工具。
  - `exoplayer`：播放相关辅助。
  - `glide`：图片加载扩展。
  - `http`：网络请求和 HTTP 辅助。
  - `rhino`：脚本执行桥接。
  - `source`：书源相关辅助。
  - `storage`：文件和存储辅助。
  - `update`：更新相关逻辑。
- `lib`：项目内库代码和自定义基础控件。
- `model`：核心业务模型和解析流程。
  - `analyzeRule`：规则解析。
  - `localBook`：本地书籍处理。
  - `remote`：远程交互模型。
  - `rss`：RSS 订阅模型。
  - `webBook`：网络书籍模型。
- `receiver`：Android BroadcastReceiver。
- `service`：Android Service 和后台服务。
- `ui`：界面层。
  - `about`：关于界面。
  - `association`：导入和关联处理。
  - `book`：书籍相关界面，包括阅读、搜索、书源、目录、下载、本地导入等。
  - `config`：应用配置界面。
  - `main`：主界面。
  - `replaceRule`：替换净化界面。
  - `rss`：订阅相关界面。
  - `welcome`：欢迎界面。
  - `widget`：自定义插件和小组件。
- `utils`：通用工具函数和扩展。
- `web`：Android 端 Web 服务相关代码。

### 资源目录

资源位于 `app/src/main/res`：

- `layout`、`layout-land`：竖屏和横屏布局。
- `values`：默认字符串、颜色、样式等资源。
- `values-zh`、`values-zh-rHK`、`values-zh-rTW`、`values-ja-rJP`、`values-es-rES`、`values-pt-rBR`、`values-vi`：多语言资源。
- `values-night`：夜间模式资源。
- `drawable`、`color`、`mipmap-*`、`anim`、`menu`、`raw`、`xml`：常规 Android 资源。

新增用户可见文案时，应同步检查默认语言和已有本地化资源。

### 测试目录

- `app/src/test`：JVM 单元测试。
- `app/src/androidTest`：Android Instrumentation 测试。
- 涉及数据库、迁移或会修改真实数据的测试命令不要直接执行，应提供给用户执行。

## 规则模块：modules/book

`modules/book` 是 Android 应用依赖的规则和内容处理模块。涉及书源规则、正文解析、发现/搜索规则、内容抽取等能力时，应优先确认这里是否已有可复用逻辑，再决定是否修改 `app` 层。

## Rhino 模块：modules/rhino

`modules/rhino` 封装 Rhino JavaScript 支持。涉及书源脚本、规则脚本执行、JS 兼容或脚本运行时行为时，应先读该模块和 `app/help/rhino` 的桥接逻辑。

## Web 前端：modules/web

`modules/web` 是独立 Vue 前端包，根目录 `package.json` 不是它的工作目录。

### 技术栈

- Vue 3
- Vite
- TypeScript
- Vue Router
- Pinia
- Element Plus
- Axios
- ESLint + Prettier

### 目录

- `api`：前端接口封装。
- `assets`：静态资源。
- `components`：复用组件。
- `config`：前端配置。
- `hooks`：组合式函数。
- `pages`：业务页面。
- `plugins`：Vue 插件和全局扩展。
- `router`：路由配置。
- `store`：Pinia 状态。
- `utils`：前端工具函数。
- `views`：视图入口。

### 前端命令

在 `modules/web` 下执行：

- `pnpm run dev`：本地开发。
- `pnpm run build`：类型检查、Vite build 和同步脚本。
- `pnpm run build-only`：仅 Vite build。
- `pnpm run type-check`：Vue TypeScript 检查。
- `pnpm run lint:fix`：ESLint 自动修复。
- `pnpm run format`：Prettier 格式化 `src/`。

按仓库 agent 规则，前端改动后还必须尝试 `pnpm run verify:policy`；若脚本不存在，需要在结果里说明。

## 关键工作流

### Android 阅读链路

阅读相关修改通常会经过：

1. 配置读取和保存：`help/config`。
2. 内容解析和加工：`help/book`、`model`、`modules/book`。
3. 阅读 Activity 和配置 Dialog：`ui/book/read`。
4. 排版、分页和文本绘制：`ui/book/read/page`。
5. 资源和文案：`res/layout`、`res/values*`。

### 数据层链路

数据层通常由 `data/entities`、`data/dao`、Room schema 和调用方模型共同构成。修改字段、DAO 或 schema 前必须先读现有实体、DAO、迁移和调用链。任何实际数据库写入或迁移命令都交给用户执行。

### Web 管理端链路

Web 端页面通常从 `router` 进入 `pages` 或 `views`，通过 `api` 调接口，通过 `store` 管理跨页面状态。能由 Android/服务/数据层完成的排序、校验和提交逻辑，不应只在 Web 前端临时处理。
