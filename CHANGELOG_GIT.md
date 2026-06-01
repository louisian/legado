# CHANGELOG_GIT.md

本文档记录当前仓库近期 git 修改和分支上下文。长期架构说明维护在 `ARCHITECTURE.md`，agent 操作规则维护在 `AGENTS.md`。

## 当前分支上下文

- 分支：`feat/dialogue-highlight`
- 近期主题：阅读页对话高亮、对话颜色配置、构建脚本和包名/Firebase 配置调整。

## 近期提交摘要

### 96685d8f9 - feat: refactor dialogue color selection methods and improve color handling in ReadStyleDialog

- 在 `ReadBookConfig.kt` 中补充对话颜色选择和颜色处理方法。
- 调整 `ReadBookActivity.kt` 的阅读页配置交互。
- 精简 `DialogueColorConfigDialog.kt` 中部分颜色选择逻辑。
- 在 `ReadStyleDialog.kt` 中改进对话颜色配置入口和颜色处理。

### 8e5f9fdc1 - feat: enhance dialogue color configuration with support for alternating colors and improved UI elements

- 扩展对话颜色配置，支持多组/交替颜色。
- 修改 `BookContent.kt`、`ContentProcessor.kt`、`TextColumn.kt`、`TextChapterLayout.kt`，让对话高亮信息进入正文处理和绘制链路。
- 增强 `DialogueColorConfigDialog.kt` 和 `dialog_dialogue_color_config.xml`。
- 同步更新默认、简中、繁中相关字符串资源。

### 1ce033b1c - feat: streamline dialogue detection logging and optimize character index handling

- 删除多处临时或冗余的对话检测日志。
- 优化正文布局中角色索引相关处理。
- 涉及 `ContentProcessor.kt`、`DialogueColorConfigDialog.kt`、`TextColumn.kt`、`TextChapterLayout.kt`。

### 37d399e17 - feat: improve dialogue detection logging and enhance character index tracking

- 增强 `ContentProcessor.kt` 中对话检测日志。
- 增强 `TextChapterLayout.kt` 中角色索引追踪。

### 55daeb3c2 - feat: enhance dialogue color configuration with improved logging and regex support

- 改进对话颜色配置和正则支持。
- 调整 `ReadBookConfig.kt` 的默认对话匹配规则。
- 修改 `ContentProcessor.kt`、`DialogueColorConfigDialog.kt`、`TextColumn.kt`、`TextChapterLayout.kt` 中的日志、正则和高亮处理。

### 2d0f0eba2 - feat: add build scripts for debug and release APKs, and implement dialogue color configuration

- 新增 `build_debug.bat`、`build_release.bat`、`generate_keystore.bat`。
- 增加 `ThemeButton.kt` 能力以支持颜色按钮展示。
- 接入阅读页对话颜色配置相关代码。
- 更新繁中/简中字符串资源。

### 6cad45973 - chore: update Firebase configuration and dialogue pattern regex

- 更新 `app/google-services.json`。
- 调整 `ReadBookConfig.kt` 中的对话匹配正则。
- 更新 `gradle.properties`。

### a884654c6 - fix: update applicationId from 'io.legado.app' to 'io.reader.cosmos'

- 将 `app/build.gradle` 中 `applicationId` 从 `io.legado.app` 调整为 `io.reader.cosmos`。

## 对话高亮相关文件

近期对话高亮相关需求优先检查以下链路：

- `app/src/main/java/io/legado/app/help/config/ReadBookConfig.kt`
- `app/src/main/java/io/legado/app/help/book/BookContent.kt`
- `app/src/main/java/io/legado/app/help/book/ContentProcessor.kt`
- `app/src/main/java/io/legado/app/ui/book/read/ReadBookActivity.kt`
- `app/src/main/java/io/legado/app/ui/book/read/config/DialogueColorConfigDialog.kt`
- `app/src/main/java/io/legado/app/ui/book/read/config/ReadStyleDialog.kt`
- `app/src/main/java/io/legado/app/ui/book/read/page/entities/column/TextColumn.kt`
- `app/src/main/java/io/legado/app/ui/book/read/page/provider/TextChapterLayout.kt`
- `app/src/main/res/layout/dialog_dialogue_color_config.xml`
- `app/src/main/res/values*/strings.xml`

处理该主题时，不要只改 UI；需要同时确认配置保存、正文缓存刷新、排版渲染、事件通知和多语言文案。
