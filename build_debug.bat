@echo off
chcp 65001 >nul
echo ========================================
echo Legado Debug 版本构建工具
echo ========================================
echo.
echo Debug 版本无需签名配置
echo.

echo 正在构建 Debug APK...
echo.

call gradlew.bat assembleDebug

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
echo APK 位置: app\build\outputs\apk\app\debug\
echo.

REM 尝试打开输出目录
if exist "app\build\outputs\apk\app\debug" (
    echo 正在打开输出目录...
    explorer "app\build\outputs\apk\app\debug"
)

echo.
pause

