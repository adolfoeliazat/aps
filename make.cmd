@echo off
rem =======================================================================
rem APS
rem
rem (C) Copyright 2015-2016 Vladimir Grechka
rem =======================================================================

if "%FOUNDATION_HOME%"=="" set FOUNDATION_HOME=e:\work\foundation
node %FOUNDATION_HOME%\maker\make.js %*
