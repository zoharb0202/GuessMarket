@echo off
cd /d "%~dp0"
if exist out rmdir /s /q out
if exist dist rmdir /s /q dist
mkdir out\dto out\engine out\server out\client out\war\WEB-INF\classes out\war\WEB-INF\lib dist\client\lib

dir /s /b dto\src\*.java > out\dto-sources.txt
javac --release 25 -d out\dto @out\dto-sources.txt || goto :error

dir /s /b engine\src\*.java > out\engine-sources.txt
javac --release 25 -cp out\dto -d out\engine @out\engine-sources.txt || goto :error

dir /s /b server\src\*.java > out\server-sources.txt
javac --release 25 -cp "out\dto;out\engine;lib\gson-2.11.0.jar;lib\servlet-api.jar" -d out\server @out\server-sources.txt || goto :error

dir /s /b client\src\*.java > out\client-sources.txt
javac --release 25 -cp "out\dto;lib\*;javafx-runtime\lib\*" -d out\client @out\client-sources.txt || goto :error
xcopy /e /i /y client\resources out\client >nul

jar cf out\dto.jar -C out\dto .
jar cf out\engine.jar -C out\engine .

xcopy /e /i /y out\server out\war\WEB-INF\classes >nul
copy out\dto.jar out\war\WEB-INF\lib >nul
copy out\engine.jar out\war\WEB-INF\lib >nul
copy lib\gson-2.11.0.jar out\war\WEB-INF\lib >nul
jar cf dist\guessmarket.war -C out\war .

jar cfe dist\client\client.jar guessmarket.client.main.GuessMarketClient -C out\client .
copy out\dto.jar dist\client\lib >nul
copy lib\gson-2.11.0.jar dist\client\lib >nul
copy lib\okhttp-4.9.1.jar dist\client\lib >nul
copy lib\okio-2.8.0.jar dist\client\lib >nul
copy lib\kotlin-stdlib-1.4.10.jar dist\client\lib >nul
copy lib\kotlin-stdlib-common-1.4.10.jar dist\client\lib >nul
copy lib\annotations-13.0.jar dist\client\lib >nul
copy client\run.bat dist\client\run.bat >nul
xcopy /e /i /y javafx-runtime dist\client\javafx-runtime >nul
copy readme.docx dist\readme.docx >nul

echo Build finished. guessmarket.war and the client folder are in the dist folder.
goto :eof

:error
echo Build failed.
