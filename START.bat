@echo off
echo ========================================
echo Ready Road Backend - Quick Start
echo ========================================
echo.
echo Starting Spring Boot on port 8888...
echo.

cd /d "%~dp0"
call mvnw.cmd spring-boot:run

pause

