@echo off
setlocal enabledelayedexpansion

echo ============================================
echo Overport CLI Build Script
echo ============================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install JDK 17 or higher
    pause
    exit /b 1
)

echo [1/5] Checking Java version...
java -version

REM Check if Gradle wrapper exists
if not exist "gradlew" (
    echo ERROR: gradlew not found
    echo Make sure you are running this script from the project root directory
    pause
    exit /b 1
)

echo.
echo [2/5] Cleaning previous builds...
call gradlew.bat clean --no-daemon --quiet
if %errorlevel% neq 0 (
    echo WARNING: Clean task failed, continuing anyway...
)

echo.
echo [3/5] Building Overport CLI...
call gradlew.bat :overportcli:shadowJar --no-daemon --stacktrace
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Build failed!
    echo Please check the error messages above
    pause
    exit /b 1
)

echo.
echo [4/5] Searching for built JAR...
set "JAR_PATH="
for /f "delims=" %%i in ('dir /s /b overportcli\build\libs\*.jar 2^>nul') do (
    set "JAR_PATH=%%i"
)

if "%JAR_PATH%"=="" (
    echo ERROR: Built JAR file not found
    pause
    exit /b 1
)

echo Found: %JAR_PATH%

echo.
echo [5/5] Creating distribution package...
set "DIST_DIR=dist"
if exist "%DIST_DIR%" rmdir /s /q "%DIST_DIR%"
mkdir "%DIST_DIR%"

copy "%JAR_PATH%" "%DIST_DIR%\overport-cli.jar" >nul
copy "README.md" "%DIST_DIR%\" >nul 2>&1

REM Create launcher script
(
echo @echo off
echo java -jar "%%~dp0overport-cli.jar" %%*
) > "%DIST_DIR%\overport.bat"

echo.
echo ============================================
echo Build completed successfully!
echo ============================================
echo.
echo Distribution files are in: %CD%\%DIST_DIR%
echo.
echo To run Overport CLI:
echo   cd %DIST_DIR%
echo   overport.bat --help
echo.
echo Or directly:
echo   java -jar %DIST_DIR%\overport-cli.jar --help
echo.

pause
