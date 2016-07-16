rem imdisk -a -t vm -s 2G -m r:

rmdir /q /s r:\pgsql
c:\opt\pgsql-9.6.0-beta2\bin\initdb -D r:\pgsql -U postgres -E UTF8
copy %~dp0\pg-test.conf r:\pgsql\postgresql.conf
c:\opt\pgsql-9.6.0-beta2\bin\pg_ctl start -w -D r:\pgsql
