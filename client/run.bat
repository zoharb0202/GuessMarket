@echo off
cd /d "%~dp0"

set PATH=%~dp0javafx-runtime\bin;%PATH%
java --module-path "javafx-runtime\lib" --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics -cp "client.jar;lib\*" guessmarket.client.main.GuessMarketClient
if %ERRORLEVEL% NEQ 0 pause
