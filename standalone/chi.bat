@echo off
setlocal enabledelayedexpansion

set "DIR=%~dp0"
set "JAVA_ARGS="
set "PROGRAM_ARGS="
set "GRAALVM_DIR=%JAVA_HOME%\lib\graalvm"

if exist "%GRAALVM_DIR%" (
    for %%i in (%*) do (
        set "opt=%%i"
        if "!opt!" equ "-debug" (
            set "JAVA_ARGS=!JAVA_ARGS! -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y"
        ) else if "!opt!" equ "-dump" (
            set "JAVA_ARGS=!JAVA_ARGS! -Dpolyglot.engine.AllowExperimentalOptions=true -Dgraal.PrintGraph=Network -Dgraal.Dump=Truffle:1 -Dpolyglot.engine.BackgroundCompilation=false -Dpolyglot.engine.TraceCompilation=true -Dpolyglot.engine.TraceCompilationDetails=true"
        ) else if "!opt!" equ "-disassemble" (
            set "JAVA_ARGS=!JAVA_ARGS! -Dpolyglot.engine.AllowExperimentalOptions=true -XX:CompileCommand=print,*.* -XX:CompileCommand=exclude,*OptimizedCallTarget.profiledPERoot -Dpolyglot.engine.BackgroundCompilation=false -Dpolyglot.engine.TraceCompilation=true -Dpolyglot.engine.TraceCompilationDetails=true"
        ) else if "!opt:~0,2!" equ "-J" (
            set "opt=!opt:~2!"
            set "JAVA_ARGS=!JAVA_ARGS! !opt!"
        ) else (
            set "PROGRAM_ARGS=!PROGRAM_ARGS! !opt!"
        )
    )
) else (
    echo Warning: Could not find GraalVM on %JAVA_HOME%. Running on JDK without support for compilation.
    echo.
    for %%i in (%*) do (
        set "opt=%%i"
        if "!opt!" equ "-debug" (
            set "JAVA_ARGS=!JAVA_ARGS! -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8000,suspend=y"
        ) else if "!opt!" equ "-dump" (
            echo NOTE: Ignoring -dump, only supported on GraalVM.
        ) else if "!opt!" equ "-disassemble" (
            echo NOTE: Ignoring -disassemble
        ) else if "!opt:~0,2!" equ "-J" (
            set "opt=!opt:~2!"
            set "JAVA_ARGS=!JAVA_ARGS! !opt!"
        ) else (
            set "PROGRAM_ARGS=!PROGRAM_ARGS! !opt!"
        )
    )
)


"%JAVA_HOME%\bin\java" %JAVA_ARGS% -p "%DIR%\modules" -m @@launcherClass@@ %PROGRAM_ARGS%
