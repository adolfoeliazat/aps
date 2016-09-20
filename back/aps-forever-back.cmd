@echo off

java ^
-cp %~dp0out;^
%~dp0lib\kotlin-runtime.jar;^
%~dp0lib\kotlin-reflect.jar; ^
aps.back.ForeverBackKt %*

