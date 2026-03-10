@echo off
setlocal
cd /d %~dp0

set MAIN_CLASS=com.smu8.game.L21Game
set BUILD_DIR=build
set CLASSES_DIR=%BUILD_DIR%\classes
set DIST_DIR=%BUILD_DIR%\dist
set MANIFEST=%BUILD_DIR%\manifest.mf
set JAR_FILE=%DIST_DIR%\L21Game.jar

if exist "%CLASSES_DIR%" rmdir /s /q "%CLASSES_DIR%"
if exist "%DIST_DIR%" rmdir /s /q "%DIST_DIR%"

mkdir "%CLASSES_DIR%"
mkdir "%DIST_DIR%"

javac -encoding UTF-8 -cp src -d "%CLASSES_DIR%" src\com\smu8\game\L21Game.java
if errorlevel 1 (
  echo [ERROR] javac failed.
  exit /b 1
)

xcopy /y /i src\com\smu8\game\*.png "%CLASSES_DIR%\com\smu8\game\" > nul
if errorlevel 1 (
  echo [ERROR] resource copy failed.
  exit /b 1
)

echo Main-Class: %MAIN_CLASS%> "%MANIFEST%"
jar cfm "%JAR_FILE%" "%MANIFEST%" -C "%CLASSES_DIR%" .
if errorlevel 1 (
  echo [ERROR] jar creation failed.
  exit /b 1
)

echo.
echo [OK] Created: %JAR_FILE%
echo [RUN] java -jar "%JAR_FILE%"
endlocal
