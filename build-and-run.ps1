# Curl API Proxy - Build and Run Script for PowerShell
# This script helps build, test, and run the Curl API Proxy project

Write-Host "================================" -ForegroundColor Cyan
Write-Host " Curl API Proxy Build Script" -ForegroundColor Cyan  
Write-Host "================================" -ForegroundColor Cyan
Write-Host ""

# Check if Java is installed
try {
    $javaVersion = java -version 2>&1
    Write-Host "✓ Java is available" -ForegroundColor Green
} catch {
    Write-Host "✗ ERROR: Java is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Java 21 and ensure it's in your PATH" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Check if Maven is installed  
try {
    $mavenVersion = mvn -version 2>&1
    Write-Host "✓ Maven is available" -ForegroundColor Green
} catch {
    Write-Host "✗ ERROR: Maven is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Maven and ensure it's in your PATH" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# Check if curl is available
try {
    $curlVersion = curl --version 2>&1
    Write-Host "✓ curl is available" -ForegroundColor Green
} catch {
    Write-Host "⚠ WARNING: curl is not installed or not in PATH" -ForegroundColor Yellow
    Write-Host "The application may not work properly without curl" -ForegroundColor Yellow
    Write-Host "Please install curl or ensure it's in your PATH" -ForegroundColor Yellow
    Read-Host "Press Enter to continue"
}

Write-Host ""

function Show-Menu {
    Write-Host "Please select an option:" -ForegroundColor White
    Write-Host "1. Clean and compile" -ForegroundColor White
    Write-Host "2. Run tests only" -ForegroundColor White
    Write-Host "3. Build JAR (skip tests)" -ForegroundColor White
    Write-Host "4. Build JAR (with tests)" -ForegroundColor White
    Write-Host "5. Run application" -ForegroundColor White
    Write-Host "6. Run with specific profile" -ForegroundColor White
    Write-Host "7. Generate test reports" -ForegroundColor White
    Write-Host "8. Check for security vulnerabilities" -ForegroundColor White
    Write-Host "9. View application info" -ForegroundColor White
    Write-Host "10. Quick test endpoints" -ForegroundColor White
    Write-Host "0. Exit" -ForegroundColor White
    Write-Host ""
}

function Invoke-Compile {
    Write-Host ""
    Write-Host "===== Cleaning and Compiling =====" -ForegroundColor Yellow
    mvn clean compile
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Compilation failed!" -ForegroundColor Red
        Read-Host "Press Enter to continue"
        return
    }
    Write-Host "✓ Compilation successful!" -ForegroundColor Green
    Read-Host "Press Enter to continue"
}

function Invoke-Test {
    Write-Host ""
    Write-Host "===== Running Tests =====" -ForegroundColor Yellow
    mvn test
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Tests failed!" -ForegroundColor Red
        Read-Host "Press Enter to continue"
        return
    }
    Write-Host "✓ All tests passed!" -ForegroundColor Green
    Read-Host "Press Enter to continue"
}

function Invoke-BuildSkipTests {
    Write-Host ""
    Write-Host "===== Building JAR (Skipping Tests) =====" -ForegroundColor Yellow
    mvn clean package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Build failed!" -ForegroundColor Red
        Read-Host "Press Enter to continue"
        return
    }
    Write-Host "✓ Build successful! JAR created in target/ directory" -ForegroundColor Green
    Read-Host "Press Enter to continue"
}

function Invoke-BuildWithTests {
    Write-Host ""
    Write-Host "===== Building JAR (With Tests) =====" -ForegroundColor Yellow
    mvn clean package
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Build failed!" -ForegroundColor Red
        Read-Host "Press Enter to continue"
        return
    }
    Write-Host "✓ Build successful! JAR created in target/ directory" -ForegroundColor Green
    Read-Host "Press Enter to continue"
}

function Invoke-Run {
    Write-Host ""
    Write-Host "===== Running Application =====" -ForegroundColor Yellow
    Write-Host "Starting Curl API Proxy on port 8080..." -ForegroundColor Cyan
    Write-Host "Press Ctrl+C to stop the application" -ForegroundColor Cyan
    Write-Host ""
    mvn spring-boot:run
    Read-Host "Press Enter to continue"
}

function Invoke-RunProfile {
    Write-Host ""
    Write-Host "Available profiles:" -ForegroundColor White
    Write-Host "- dev (development)" -ForegroundColor White
    Write-Host "- test (testing)" -ForegroundColor White
    Write-Host "- prod (production)" -ForegroundColor White
    Write-Host ""
    $profile = Read-Host "Enter profile name"
    Write-Host ""
    Write-Host "===== Running Application with Profile: $profile =====" -ForegroundColor Yellow
    Write-Host "Starting Curl API Proxy with profile '$profile' on port 8080..." -ForegroundColor Cyan
    Write-Host "Press Ctrl+C to stop the application" -ForegroundColor Cyan
    Write-Host ""
    mvn spring-boot:run "-Dspring-boot.run.profiles=$profile"
    Read-Host "Press Enter to continue"
}

function Invoke-TestReports {
    Write-Host ""
    Write-Host "===== Generating Test Reports =====" -ForegroundColor Yellow
    mvn test surefire-report:report
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Report generation failed!" -ForegroundColor Red
        Read-Host "Press Enter to continue"
        return
    }
    Write-Host "✓ Test reports generated!" -ForegroundColor Green
    if (Test-Path "target\site\surefire-report.html") {
        Start-Process "target\site\surefire-report.html"
    }
    Read-Host "Press Enter to continue"
}

function Invoke-SecurityCheck {
    Write-Host ""
    Write-Host "===== Checking for Security Vulnerabilities =====" -ForegroundColor Yellow
    mvn org.owasp:dependency-check-maven:check
    if ($LASTEXITCODE -ne 0) {
        Write-Host "ERROR: Security check failed!" -ForegroundColor Red
        Read-Host "Press Enter to continue"
        return
    }
    Write-Host "✓ Security check completed!" -ForegroundColor Green
    Read-Host "Press Enter to continue"
}

function Show-AppInfo {
    Write-Host ""
    Write-Host "===== Application Information =====" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Project: Curl API Proxy" -ForegroundColor White
    Write-Host "Port: 8080" -ForegroundColor White
    Write-Host "Base URL: http://localhost:8080/api/curl" -ForegroundColor White
    Write-Host ""
    Write-Host "Available Endpoints:" -ForegroundColor Cyan
    Write-Host "- GET  /api/curl/health" -ForegroundColor White
    Write-Host "- POST /api/curl/execute" -ForegroundColor White
    Write-Host "- POST /api/curl/execute-raw" -ForegroundColor White
    Write-Host "- POST /api/curl/get" -ForegroundColor White
    Write-Host "- POST /api/curl/post" -ForegroundColor White
    Write-Host ""
    Read-Host "Press Enter to continue"
}

function Test-Endpoints {
    Write-Host ""
    Write-Host "===== Quick Endpoint Tests =====" -ForegroundColor Yellow
    Write-Host "Testing health endpoint..." -ForegroundColor Cyan
    
    try {
        $healthResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/curl/health" -Method Get -ErrorAction Stop
        Write-Host "✓ Health endpoint: $healthResponse" -ForegroundColor Green
    } catch {
        Write-Host "✗ Health endpoint failed - Is the application running?" -ForegroundColor Red
    }
    
    Write-Host ""
    Write-Host "Testing raw curl endpoint with HTTPBin..." -ForegroundColor Cyan
    
    $curlRequest = @{
        url = "https://httpbin.org/get"
        parameters = "-X GET"
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri "http://localhost:8080/api/curl/execute-raw" -Method Post -Body $curlRequest -ContentType "application/json" -ErrorAction Stop
        Write-Host "✓ Raw curl endpoint successful" -ForegroundColor Green
        Write-Host "Response: $($response.output.Substring(0, [Math]::Min(100, $response.output.Length)))..." -ForegroundColor Gray
    } catch {
        Write-Host "✗ Raw curl endpoint failed - Is the application running?" -ForegroundColor Red
    }
    
    Read-Host "Press Enter to continue"
}

# Main loop
do {
    Clear-Host
    Write-Host "================================" -ForegroundColor Cyan
    Write-Host " Curl API Proxy Build Script" -ForegroundColor Cyan  
    Write-Host "================================" -ForegroundColor Cyan
    Write-Host ""
    
    Show-Menu
    $choice = Read-Host "Enter your choice (0-10)"
    
    switch ($choice) {
        "1" { Invoke-Compile }
        "2" { Invoke-Test }
        "3" { Invoke-BuildSkipTests }
        "4" { Invoke-BuildWithTests }
        "5" { Invoke-Run }
        "6" { Invoke-RunProfile }
        "7" { Invoke-TestReports }
        "8" { Invoke-SecurityCheck }
        "9" { Show-AppInfo }
        "10" { Test-Endpoints }
        "0" { 
            Write-Host ""
            Write-Host "Goodbye!" -ForegroundColor Green
            exit 0
        }
        default {
            Write-Host "Invalid choice. Please try again." -ForegroundColor Red
            Start-Sleep -Seconds 1
        }
    }
} while ($true)