@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  DiscordBot startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and DISCORD_BOT_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\DiscordBot-1.0.jar;%APP_HOME%\lib\jda-nas-1.1.0.jar;%APP_HOME%\lib\google-api-services-youtube-v3-rev222-1.25.0.jar;%APP_HOME%\lib\logback-classic-1.2.3.jar;%APP_HOME%\lib\jda-chewtils-examples-1.22.0.jar;%APP_HOME%\lib\jda-chewtils-command-1.22.0.jar;%APP_HOME%\lib\jda-chewtils-oauth2-1.22.0.jar;%APP_HOME%\lib\json-20200518.jar;%APP_HOME%\lib\JDA-5.0.0-alpha.4.jar;%APP_HOME%\lib\gson-2.8.6.jar;%APP_HOME%\lib\poi-ooxml-3.9.jar;%APP_HOME%\lib\poi-3.9.jar;%APP_HOME%\lib\lavaplayer-fork-1.3.97.jar;%APP_HOME%\lib\jda-chewtils-doc-1.22.0.jar;%APP_HOME%\lib\jda-chewtils-menu-1.22.0.jar;%APP_HOME%\lib\jda-chewtils-commons-1.22.0.jar;%APP_HOME%\lib\udp-queue-1.0.8.jar;%APP_HOME%\lib\lava-common-1.1.2.jar;%APP_HOME%\lib\lavaplayer-common-1.0.6.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\google-api-client-1.25.0.jar;%APP_HOME%\lib\logback-core-1.2.3.jar;%APP_HOME%\lib\google-oauth-client-1.25.0.jar;%APP_HOME%\lib\google-http-client-jackson2-1.25.0.jar;%APP_HOME%\lib\google-http-client-1.25.0.jar;%APP_HOME%\lib\jsr305-3.0.2.jar;%APP_HOME%\lib\annotations-16.0.1.jar;%APP_HOME%\lib\nv-websocket-client-2.14.jar;%APP_HOME%\lib\okhttp-3.13.0.jar;%APP_HOME%\lib\opus-java-1.1.1.jar;%APP_HOME%\lib\commons-collections4-4.1.jar;%APP_HOME%\lib\trove4j-3.0.3.jar;%APP_HOME%\lib\jackson-databind-2.10.1.jar;%APP_HOME%\lib\httpclient-4.5.10.jar;%APP_HOME%\lib\commons-codec-1.11.jar;%APP_HOME%\lib\poi-ooxml-schemas-3.9.jar;%APP_HOME%\lib\dom4j-1.6.1.jar;%APP_HOME%\lib\jackson-core-2.10.1.jar;%APP_HOME%\lib\lavaplayer-natives-1.3.14.jar;%APP_HOME%\lib\commons-io-2.6.jar;%APP_HOME%\lib\jsoup-1.12.1.jar;%APP_HOME%\lib\base64-2.3.9.jar;%APP_HOME%\lib\guava-20.0.jar;%APP_HOME%\lib\okio-1.17.2.jar;%APP_HOME%\lib\opus-java-api-1.1.1.jar;%APP_HOME%\lib\opus-java-natives-1.1.1.jar;%APP_HOME%\lib\jackson-annotations-2.10.1.jar;%APP_HOME%\lib\xmlbeans-2.3.0.jar;%APP_HOME%\lib\xml-apis-1.0.b2.jar;%APP_HOME%\lib\httpcore-4.4.12.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\jna-4.4.0.jar;%APP_HOME%\lib\stax-api-1.0.1.jar;%APP_HOME%\lib\j2objc-annotations-1.1.jar

@rem Execute DiscordBot
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %DISCORD_BOT_OPTS%  -classpath "%CLASSPATH%" sabjen.DiscordBot.Bot %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable DISCORD_BOT_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%DISCORD_BOT_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
