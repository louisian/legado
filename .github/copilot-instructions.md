# Legado (阅读) - AI Coding Assistant Guide

## Project Overview
Legado is an open-source **Android novel reader** supporting custom book sources, subscriptions, local books (TXT/EPUB), and WebDAV sync. The codebase combines native Android (Kotlin) with an embedded Vue 3 web interface.

## Architecture

### Module Structure
- **`app/`**: Main Android application (min SDK 21, target SDK 36)
  - `api/`: Web API controllers for external integration
  - `data/`: Room database (v75), DAOs, entities (Book, BookSource, RssSource, etc.)
  - `model/`: Business logic (analyzeRule, webBook, localBook, rss)
  - `ui/`: MVVM Activities/Fragments with ViewBinding
  - `service/`: Background services (sync, download, TTS)
  - `web/`: Embedded NanoHttpd server for remote control
- **`modules/book/`**: Shared book parsing library
- **`modules/rhino/`**: Custom JavaScript engine (forked Rhino)
- **`modules/web/`**: Vue 3 frontend (bookshelf + source editor at `:8080`)

### Key Technologies
- **Kotlin 2.3.0** + Coroutines 1.10.2
- **Room DB** with manual migrations (see `DatabaseMigrations.kt`)
- **OkHttp 5.3.2** for networking
- **Glide 5.0.5** for images
- **Jsoup 1.16.2** (frozen version - breaking changes in newer releases)
- **WebDAV** for cloud backup/sync
- **NanoHttpd** for embedded web server

## Critical Conventions

### Database Management
- **Schema version 75** with manual migrations in [`DatabaseMigrations.kt`](app/src/main/java/io/legado/app/data/DatabaseMigrations.kt)
- Room schemas exported to [`app/schemas/`](app/schemas/io.legado.app.data.AppDatabase/)
- Migration tests in [`MigrationTest.kt`](app/src/androidTest/java/io/legado/app/MigrationTest.kt)
- When adding DB changes: update `AppDatabase` version, add migration, update schema JSON

### Version Pinning (DO NOT UPGRADE)
```toml
# Jsoup 1.16.2 - newer versions break JsoupXpath parsing
# Protobuf 4.26.1 - newer versions have compatibility issues
# HutTool 5.8.22 - breaking changes in newer versions
```
See [`libs.versions.toml`](gradle/libs.versions.toml) for rationale.

### Build System
- Uses Gradle **version catalogs** (`libs.versions.toml`)
- Release builds: `name_version` = `legado_3.YYMMDDH` (GMT+8)
- ProGuard enabled in release with custom rules for Cronet
- Signing configs via `RELEASE_STORE_FILE` properties

### Code Style
- **MVVM pattern**: Activities use `VMBaseActivity<VB, VM>` with ViewBinding
- Singletons use `object` (e.g., `AppWebDav`, `DefaultData`)
- Extension functions in `*Extensions.kt` files (e.g., `BookExtensions.kt`)
- No Android components in `model/` layer - use `appDb` directly

## Developer Workflows

### Building & Packaging

#### Debug Build (开发测试)
```bash
# 构建 Debug APK（无需签名）
./gradlew assembleDebug

# 构建并安装到设备
./gradlew installDebug

# 启动 Android 模拟器
./avd.sh  # macOS/Linux
./avd.bat # Windows
```

#### Release Build (正式发布)
```bash
# 1. 配置签名（首次打包需要）
# 在项目根目录创建或编辑 gradle.properties，添加：
RELEASE_STORE_FILE=your_keystore.jks
RELEASE_STORE_PASSWORD=your_store_password
RELEASE_KEY_ALIAS=your_key_alias
RELEASE_KEY_PASSWORD=your_key_password

# 2. 构建 Release APK
./gradlew assembleAppRelease

# 3. 生成的 APK 位置：
# app/build/outputs/apk/app/release/legado_app_3.YYMMDDH.apk
```

**版本命名规则**：
- 格式：`legado_3.YYMMDDH` （东八区时间）
- versionCode：`10000 + git commit count`
- 示例：`legado_app_3.241230H.apk`

**Product Flavors**：
- `app`：标准版本（主要发行版）
- `google`：Google Play 版本（CI 专用）

**构建类型**：
- `debug`：开发版（包名：`io.legado.app.debug`）
- `release`：发布版（包名：`io.legado.app.release`）
  - 启用 ProGuard 混淆和资源压缩
  - 需要签名配置才能构建

### Web Module Development
```bash
cd modules/web
pnpm dev  # Requires running Android app for backend API
```
- **Routes**: `/` (bookshelf), `/#/bookSource`, `/#/rssSource`
- Auto-syncs dist to `app/assets/web/` via `sync.js` on build

### CI/CD Pipeline
GitHub Actions 自动化构建（`.github/workflows/`）：
- **release.yml**: 正式版本发布（手动触发）
  - 构建签名 APK（需要 secrets 配置）
  - 自动创建 GitHub Release
  - 上传到 Artifact 和 Release
- **test.yml**: Pull Request 测试构建
- **cronet.yml**: Cronet 库下载管理
- **web.yml**: Web 前端部署

**Secrets 配置**（仅维护者）：
- `RELEASE_KEY_STORE`: Base64 编码的 keystore
- `RELEASE_KEY_ALIAS`/`RELEASE_KEY_PASSWORD`
- `RELEASE_STORE_PASSWORD`
- `SERVICE_ACCOUNT_JSON`: Google Play 发布（可选）

### Database Operations
- **View DB**: Room schemas in `app/schemas/io.legado.app.data.AppDatabase/{version}.json`
- **Shrink DB**: Settings → Advanced → Shrink Database (`VACUUM`)
- **WebDAV backup**: Auto-uploaded as `backup{timestamp}.zip`

### Testing Book Sources
Use Web API (requires "Web Service" enabled in settings):
```bash
# Insert book source (single)
POST http://localhost:1234/saveBookSource
Content-Type: application/json

# Get all book sources
GET http://localhost:1234/getBookSources
```
See [`api.md`](api.md) for full API reference.

## Integration Points

### WebDAV Sync
- Handles book progress (`BookProgress`) and full backups
- Implementation: [`AppWebDav.kt`](app/src/main/java/io/legado/app/help/AppWebDav.kt)
- Server config stored in `Server` entity (Room)

### Custom Book Sources
- JSON schema: [`BookSource.kt`](app/src/main/java/io/legado/app/data/entities/BookSource.kt)
- Rule parsing: [`analyzeRule/`](app/src/main/java/io/legado/app/model/analyzeRule/)
- JSoup/XPath/JsonPath/Regex support

### JavaScript Execution
- Custom Rhino fork in `modules/rhino/`
- Used for book source JS evaluation and dynamic rules

## Modifying the Reader

### Reader Architecture
阅读器核心代码位于 `app/src/main/java/io/legado/app/ui/book/read/`：

**主要组件**：
- [`ReadBookActivity.kt`](app/src/main/java/io/legado/app/ui/book/read/ReadBookActivity.kt) - 阅读器主 Activity（1700+ 行）
- [`ReadBookViewModel.kt`](app/src/main/java/io/legado/app/ui/book/read/ReadBookViewModel.kt) - 阅读业务逻辑
- [`ReadView.kt`](app/src/main/java/io/legado/app/ui/book/read/page/ReadView.kt) - 阅读内容容器
- [`ContentTextView.kt`](app/src/main/java/io/legado/app/ui/book/read/page/ContentTextView.kt) - 文本内容绘制
- [`ReadMenu.kt`](app/src/main/java/io/legado/app/ui/book/read/ReadMenu.kt) - 阅读菜单

**布局文件**：
- [`activity_book_read.xml`](app/src/main/res/layout/activity_book_read.xml) - 主布局
- [`view_read_menu.xml`](app/src/main/res/layout/view_read_menu.xml) - 菜单布局

### Page Rendering System
翻页和排版系统在 `page/` 目录：

**核心类**：
- `provider/ChapterProvider.kt` - 章节内容提供者（排版引擎）
- `provider/TextPageFactory.kt` - 页面工厂（生成 TextPage）
- `delegate/` - 翻页动画代理（横向、竖向、覆盖、仿真等）
- `entities/TextPage.kt` - 页面数据模型

**排版流程**：
```kotlin
// ChapterProvider 负责将章节文本排版成页面
ChapterProvider.getTextPage() 
  → TextChapterLayout.layout()  // 计算行列位置
  → TextPage 包含 TextLine 列表
  → ContentTextView.onDraw() 绘制
```

### Configuration System
阅读配置在 `config/` 和 `help/config/`：

**配置类**：
- `ReadBookConfig.kt` - 阅读配置（字体、颜色、间距等）
- `ReadTipConfig.kt` - 页眉页脚提示配置
- `ReadStyleDialog.kt` - 样式配置对话框
- `BgTextConfigDialog.kt` - 背景和文字配置

**修改阅读样式示例**：
```kotlin
// 修改字体大小
ReadBookConfig.config.textSize = 18

// 修改行距
ReadBookConfig.config.lineSpacingExtra = 1.2f

// 应用配置
ChapterProvider.upTextSize()
ReadBook.loadContent(resetPageOffset = true)
```

### Common Modifications

**修改翻页动画**：
编辑 `page/delegate/` 下的翻页代理类（如 `HorizontalPageDelegate.kt`）

**修改字体渲染**：
调整 `ContentTextView.onDraw()` 方法中的 `Paint` 参数

**修改页面布局**：
调整 `provider/ChapterProvider.kt` 中的 `visibleRect` 和边距计算

**添加自定义菜单项**：
在 `ReadMenu.kt` 中添加按钮和对应的点击事件

**修改文本选择行为**：
编辑 `ContentTextView.kt` 中的选择逻辑（`selectStart`/`selectEnd`）

### Key Dependencies
- **ReadBook (model)**: 全局阅读状态管理器
- **ReadAloud (model)**: 朗读引擎
- **BookHelp (help)**: 书籍缓存管理
- **AppConfig**: 应用全局配置

### Testing Reader Changes
```bash
# 构建并安装到设备
./gradlew installDebug

# 打开任意书籍测试阅读器功能
```

## Common Patterns

### Loading External Content
```kotlin
// Use coroutines with IO dispatcher
execute {
    val source = appDb.bookSourceDao.getByKey(url)
    WebBook.getBookInfo(source, book)
}.onSuccess {
    // Update UI
}
```

### ViewBinding in Activities
```kotlin
class MyActivity : VMBaseActivity<ActivityMyBinding, MyViewModel>() {
    override val binding by viewBinding(ActivityMyBinding::inflate)
    override val viewModel by viewModels<MyViewModel>()
}
```

### Database Transactions
```kotlin
appDb.runInTransaction {
    appDb.bookDao.insert(book)
    appDb.bookChapterDao.insert(*chapters)
}
```

## Debugging Tips
- **Logs**: Check `AppLog` for structured logging
- **Web interface**: Access at `http://<device-ip>:<port>` when Web Service is on
- **Database inspection**: Use `app/schemas/` JSON files or Android Studio's DB inspector
- **Cronet debugging**: See [`cronet.json`](app/src/main/assets/cronet.json) config

## Localization
- Primary: Simplified Chinese (`values-zh/`)
- Secondary: English (`values/`)
- App name variants: `@string/app_name` vs `@string/app_name_a` (for release variants)

## References
- [Official Wiki](https://www.yuque.com/legado/wiki)
- [Book Source Rules](https://mgz0227.github.io/The-tutorial-of-Legado/)
- [Update Log](app/src/main/assets/updateLog.md)
