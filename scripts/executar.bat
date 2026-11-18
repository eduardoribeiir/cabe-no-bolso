@echo off
rem Executa a aplicacao (compile antes com scripts\compilar.bat)
cd /d "%~dp0.."
java -cp out cabenobolso.Main
