@echo off
echo ===================================
echo Running Test Verification
echo ===================================
cd /d C:\Users\fqsdg\Desktop\end_project\readyroad
call mvnw.cmd test -Dtest=ExamServiceIntegrationTest
echo.
echo ===================================
echo Test Run Complete
echo ===================================
pause
