@echo off
cd /d "%~dp0"

java -cp "engine.jar;ui.jar" guessmarket.ui.MainApp 2>nul
if %ERRORLEVEL% EQU 0 goto :eof

echo Starting the application using the bundled JavaFX runtime...
set PATH=%~dp0javafx-runtime\bin;%PATH%
java --module-path "javafx-runtime\lib" --add-modules javafx.controls,javafx.fxml -cp "engine.jar;ui.jar" guessmarket.ui.MainApp
pause