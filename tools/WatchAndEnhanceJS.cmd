@echo off

echo Kill me, I'm worthless
exit /b 1

java -cp %~dp0..\lib\kotlin\1.1-m02-eap\kotlin-runtime.jar;%~dp0..\back\out;%~dp0out aps.WatchAndEnhanceJSKt

