@echo off
cd /d C:\Users\fqsdg\Desktop\end_project\readyroad
echo Running tests...
call mvnw.cmd clean test > test_results.log 2>&1
echo.
echo ==== TEST SUMMARY ====
findstr /C:"Tests run" /C:"BUILD" /C:"Failures" /C:"Errors" /C:"Available" test_results.log
echo.
echo Full log saved to: test_results.log
pause
