@echo off
chcp 65001 >nul
echo ========================================
echo Legado 密钥库生成工具
echo ========================================
echo.

set KEYSTORE_FILE=legado_release.jks
set KEY_ALIAS=legado_key

echo 即将生成签名密钥库...
echo 文件名: %KEYSTORE_FILE%
echo 别名: %KEY_ALIAS%
echo.
echo 请按照提示输入信息：
echo - 密钥库口令（请牢记）
echo - 姓名、组织等信息（可随意填写）
echo.
pause

keytool -genkey -v -keystore %KEYSTORE_FILE% -keyalg RSA -keysize 2048 -validity 10000 -alias %KEY_ALIAS%

if errorlevel 1 (
    echo.
    echo ❌ 密钥库生成失败！
    echo 请检查是否已安装 Java JDK
    pause
    exit /b 1
)

echo.
echo ========================================
echo ✅ 密钥库生成成功！
echo ========================================
echo.
echo 文件位置: %cd%\%KEYSTORE_FILE%
echo.
echo 下一步：
echo 1. 编辑 gradle.properties 文件
echo 2. 取消以下行的注释并填写信息：
echo    RELEASE_STORE_FILE=%KEYSTORE_FILE%
echo    RELEASE_STORE_PASSWORD=你刚才设置的密钥库密码
echo    RELEASE_KEY_ALIAS=%KEY_ALIAS%
echo    RELEASE_KEY_PASSWORD=你刚才设置的密钥密码
echo.
echo 3. 运行构建命令：
echo    gradlew.bat assembleAppRelease
echo.
pause

