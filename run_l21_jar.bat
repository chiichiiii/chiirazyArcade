@echo off
setlocal
cd /d %~dp0

set JAR_FILE=build\dist\L21Game.jar

if not exist "%JAR_FILE%" (
  echo [INFO] JAR not found. Building first...
  call build_l21_jar.bat
  if errorlevel 1 exit /b 1
)

java -jar "%JAR_FILE%"
endlocal
