rmdir /q /s r:\pgsql
%PG_HOME%\bin\initdb -D r:\pgsql -U postgres -E UTF8
copy %~dp0\pg-test.conf r:\pgsql\postgresql.conf
%PG_HOME%\bin\pg_ctl start -w -D r:\pgsql

