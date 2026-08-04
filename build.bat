@echo off
REM builds engine.jar and console.jar into the dist folder
cd /d "%~dp0"
if exist out rmdir /s /q out
if exist dist rmdir /s /q dist
mkdir out\engine out\console dist

dir /s /b engine\src\*.java > out\engine-sources.txt
javac -d out\engine @out\engine-sources.txt || goto :error

dir /s /b console\src\*.java > out\console-sources.txt
javac -cp out\engine -d out\console @out\console-sources.txt || goto :error

jar cf dist\engine.jar -C out\engine .
jar cfm dist\console.jar manifest.txt -C out\console .
copy run.bat dist\run.bat >nul

echo Build finished. The jars are in the dist folder.
goto :eof

:error
echo Build failed.
