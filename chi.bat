@echo off

set "DIR=%~dp0"
"%DIR%\standalone\target\chi.bat" "--modules=std.chim,aoc.chim" %*
