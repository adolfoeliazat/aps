@echo off
rem =======================================================================
rem APS
rem
rem (C) Copyright 2015-2016 Vladimir Grechka
rem =======================================================================

java ^
-cp %~dp0out;^
%~dp0..\lib\kotlin\1.1-m02-eap\kotlin-runtime.jar;^
%~dp0..\lib\kotlin\1.1-m02-eap\kotlin-reflect.jar; ^
aps.back.RunKt %*

