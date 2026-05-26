@echo off
REM Setup script for Traders Guardian Supabase Connection

echo.
echo ========================================
echo Traders Guardian - Supabase Setup
echo ========================================
echo.

REM Check if .env file exists
if exist ".env" (
    echo [OK] .env file already exists
    echo.
    echo Current configuration:
    type .env
    echo.
    choice /C YN /M "Do you want to reconfigure? (Y/N)"
    if errorlevel 2 goto done
    if errorlevel 1 goto configure
) else (
    goto configure
)

:configure
echo.
echo Please provide your Supabase credentials:
echo (You can find these at: https://app.supabase.com > Settings > Database > Connection Details)
echo.

set /p SUPABASE_HOST="Enter SUPABASE_HOST [aws-1-ap-south-1.pooler.supabase.com]: " || set "SUPABASE_HOST=aws-1-ap-south-1.pooler.supabase.com"
set /p SUPABASE_PORT="Enter SUPABASE_PORT [5432]: " || set "SUPABASE_PORT=5432"
set /p SUPABASE_USER="Enter SUPABASE_USER [postgres.ixmvlhdqrcokeqhrdifr]: " || set "SUPABASE_USER=postgres.ixmvlhdqrcokeqhrdifr"
set /p SUPABASE_PASSWORD="Enter SUPABASE_PASSWORD: "

REM Create .env file
(
    echo SUPABASE_HOST=%SUPABASE_HOST%
    echo SUPABASE_PORT=%SUPABASE_PORT%
    echo SUPABASE_USER=%SUPABASE_USER%
    echo SUPABASE_PASSWORD=%SUPABASE_PASSWORD%
) > .env

echo.
echo [OK] .env file created successfully!
echo.

choice /C YN /M "Do you want to start the application now? (Y/N)"
if errorlevel 2 goto done
if errorlevel 1 goto start

:start
echo.
echo Starting Traders Guardian...
echo.
call mvnw.cmd spring-boot:run
goto done

:done
echo.
echo Setup complete! Application is running on http://localhost:8080
echo.
pause
