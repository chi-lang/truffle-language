@echo off

set "DIR=%~dp0"
"%DIR%\standalone\target\chi.bat" "--modules=D:\dev\chi-compiler\chi-stdlib\mods\std.chim" %*
