@echo off
echo ============================================================
echo   COMPILING MCA RESUME PROJECT: AI JOB PORTAL (JAVA BACKEND)
echo ============================================================

if not exist bin mkdir bin

echo Compiling Java source files...
javac -d bin -sourcepath src src\com\jobportal\model\*.java src\com\jobportal\ai\*.java src\com\jobportal\db\*.java src\com\jobportal\handler\*.java src\com\jobportal\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Java compilation failed! Please check Java errors above.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo ✅ Compilation successful!
echo Starting Java Job Portal Web Server...
echo.

java -cp bin com.jobportal.Main 8080
