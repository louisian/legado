@echo off
chcp 65001 >nul
echo ========================================
echo Legado Release 版本构建工具
echo ========================================
echo.

REM 检查是否配置了签名
findstr /C:"RELEASE_STORE_FILE" gradle.properties | findstr /V "#" >nul
if errorlevel 1 (
    echo ❌ 未找到签名配置！
    echo.
    echo 请先完成以下步骤：
    echo 1. 运行 generate_keystore.bat 生成密钥库
    echo 2. 编辑 gradle.properties 配置签名信息
    echo.
    pause
    exit /b 1
)

echo 正在构建 Release APK...
echo.
echo 构建命令: gradlew.bat clean assembleAppRelease
echo.

call gradlew.bat clean assembleAppRelease

if errorlevel 1 (
    echo.
    echo ❌ 构建失败！请检查错误信息
    pause
    exit /b 1
)

echo.
echo ========================================
echo ✅ 构建成功！
echo ========================================
echo.
echo APK 位置: app\build\outputs\apk\app\release\
echo.

REM 尝试打开输出目录
if exist "app\build\outputs\apk\app\release" (
    echo 正在打开输出目录...
    explorer "app\build\outputs\apk\app\release"
)

echo.
pause

