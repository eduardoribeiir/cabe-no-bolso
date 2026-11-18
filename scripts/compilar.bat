@echo off
rem Compila todos os .java para a pasta out\
setlocal EnableDelayedExpansion
cd /d "%~dp0.."
if not exist out mkdir out
> sources.txt (
    for /r src %%f in (*.java) do (
        set "source=%%f"
        echo "!source:\=/!"
    )
)
javac -encoding UTF-8 -d out @sources.txt
if %errorlevel% neq 0 exit /b %errorlevel%
echo Compilado com sucesso em out\
