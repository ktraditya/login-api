@echo off
REM Curl API Proxy - Build and Run Script for Windows
REM This script helps build, test, and run the Curl API Proxy project

echo ================================
echo  Curl API Proxy Build Script
echo ================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 21 and ensure it's in your PATH
    pause
    exit /b 1
)

REM Check if Maven is installed
mvn -version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven and ensure it's in your PATH
    pause
    exit /b 1
)

REM Check if curl is available
curl --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo WARNING: curl is not installed or not in PATH
    echo The application may not work properly without curl
    echo Please install curl or ensure it's in your PATH
    pause
)

echo Java and Maven are available!
echo.

:MENU
echo Please select an option:
echo 1. Clean and compile
echo 2. Run tests only
echo 3. Build JAR (skip tests)
echo 4. Build JAR (with tests)
echo 5. Run application
echo 6. Run with specific profile
echo 7. Generate test reports
echo 8. Check for security vulnerabilities
echo 9. View application logs
echo 0. Exit
echo.
set /p choice=Enter your choice (0-9): 

if "%choice%"=="1" goto COMPILE
if "%choice%"=="2" goto TEST
if "%choice%"=="3" goto BUILD_SKIP_TESTS
if "%choice%"=="4" goto BUILD_WITH_TESTS
if "%choice%"=="5" goto RUN
if "%choice%"=="6" goto RUN_PROFILE
if "%choice%"=="7" goto TEST_REPORTS
if "%choice%"=="8" goto SECURITY_CHECK
if "%choice%"=="9" goto VIEW_LOGS
if "%choice%"=="0" goto EXIT
goto MENU

:COMPILE
echo.
echo ===== Cleaning and Compiling =====
mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed!
    pause
    goto MENU
)
echo Compilation successful!
pause
goto MENU

:TEST
echo.
echo ===== Running Tests =====
mvn test
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Tests failed!
    pause
    goto MENU
)
echo All tests passed!
pause
goto MENU

:BUILD_SKIP_TESTS
echo.
echo ===== Building JAR (Skipping Tests) =====
mvn clean package -DskipTests
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed!
    pause
    goto MENU
)
echo Build successful! JAR created in target/ directory
pause
goto MENU

:BUILD_WITH_TESTS
echo.
echo ===== Building JAR (With Tests) =====
mvn clean package
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Build failed!
    pause
    goto MENU
)
echo Build successful! JAR created in target/ directory
pause
goto MENU

:RUN
echo.
echo ===== Running Application =====
echo Starting Curl API Proxy on port 8080...
echo Press Ctrl+C to stop the application
echo.
mvn spring-boot:run
pause
goto MENU

:RUN_PROFILE
echo.
echo Available profiles:
echo - dev (development)
echo - test (testing)  
echo - prod (production)
echo.
set /p profile=Enter profile name: 
echo.
echo ===== Running Application with Profile: %profile% =====
echo Starting Curl API Proxy with profile '%profile%' on port 8080...
echo Press Ctrl+C to stop the application
echo.
mvn spring-boot:run -Dspring-boot.run.profiles=%profile%
pause
goto MENU

:TEST_REPORTS
echo.
echo ===== Generating Test Reports =====
mvn test surefire-report:report
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Report generation failed!
    pause
    goto MENU
)
echo Test reports generated in target/site/surefire-report.html
start target\site\surefire-report.html
pause
goto MENU

:SECURITY_CHECK
echo.
echo ===== Checking for Security Vulnerabilities =====
mvn dependency-check:check
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Security check failed!
    pause
    goto MENU
)
echo Security check completed! Report available in target/dependency-check-report.html
pause
goto MENU

:VIEW_LOGS
echo.
echo ===== Application Logs =====
if exist logs\curl-api-proxy.log (
    type logs\curl-api-proxy.log
) else (
    echo No log file found. Application may not have been started with file logging enabled.
)
pause
goto MENU

:EXIT
echo.
echo Goodbye!
exit /b 0