@echo off
rem =======================================================================
rem APS
rem
rem (C) Copyright 2015-2016 Vladimir Grechka
rem =======================================================================

if "%FOUNDATION_HOME%"=="" set FOUNDATION_HOME=e:\work\foundation

..\node_modules\.bin\nodemon ^
    --watch lib ^
    --watch %FOUNDATION_HOME%\u\lib ^
    --watch E:\work\aps\aps\built\ua-writer\kotlin\front-enhanced.js ^
    lib\backend.js



rem --watch E:\work\aps\aps\built\ua-writer\aps-scala-fastopt.js ^

    