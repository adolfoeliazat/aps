@echo off
rem =======================================================================
rem APS
rem
rem (C) Copyright 2015-2016 Vladimir Grechka
rem =======================================================================

if "%FOUNDATION_HOME%"=="" set FOUNDATION_HOME=e:\work\foundation
node_modules\.bin\nodemon --watch aps\lib --watch %FOUNDATION_HOME%\u\lib aps\lib\backend

