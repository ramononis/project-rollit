@ECHO OFF
SET cc=javac
SET cflags=
SET src=src
SET res=resources
SET img=%res%\images
SET out=bin
SET lstf=temp.txt
SET manifest=%res%\ManifestClient.txt
SET versionfile=.version
FOR /F %%G IN (%versionfile%) DO SET version=%%G
SET dist=Project Rollit Client.jar
IF EXIST "%lstf%" DEL /F /Q "%lstf%"
FOR /F "usebackq tokens=*" %%G IN (`DIR /B /S "%src%\*.java"`) DO CALL :append "%%G"
IF EXIST "%out%" RMDIR /S /Q "%out%" > NUL
MKDIR "%out%"
"%cc%" %cflags% -d "%out%" "@%lstf%"
DEL /F /Q "%lstf%"
IF EXIST "%dist%" DEL /F /Q "%dist%"
IF EXIST "%lstf%" DEL /F /Q "%lstf%"
COPY "%manifest%" "%lstf%"
ECHO Specification-Version: "%version%" >> "%lstf%"
ECHO Implementation-Version: "%version%" >> "%lstf%"
jar cfm "%dist%" "%lstf%" -C "%out%" . %img%\*.png
DEL /F /Q "%lstf%"
:end
PAUSE
EXIT

:append
SET gx=%1
SET gx=%gx:\=\\%
ECHO %gx% >> %lstf%
GOTO :eof