﻿/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

NO WAY -- Prevent accidental running of whole script in pgAdmin

drop function if exists on_insert();
create or replace function on_insert()
returns trigger as $$
begin
    new.deleted = false;
    new.inserted_at = now() at time zone 'utc';
    new.updated_at = new.inserted_at;
    return new;	
end;
$$ language 'plpgsql';


drop function if exists on_update();
create or replace function on_update()
returns trigger as $$
begin
    new.updated_at = now() at time zone 'utc';
    return new;	
end;
$$ language 'plpgsql';


create table users(
    id bigserial,
    deleted boolean,
    inserted_at timestamp,
    updated_at timestamp,
    kind text,
    email text unique,
    password_hash text,
    state text,
    first_name text,
    last_name text);
create trigger on_insert before insert on users for each row execute procedure on_insert();
create trigger on_update before update on users for each row execute procedure on_update();

insert into users(email, kind, first_name, last_name, password_hash) values ('root', 'root', 'Vladimir', 'Grechka', '$2a$10$bWP5kkNWANH3S2C4c0hgbuhR1uZBXiW84OMzcoTvY559e8azTcXcK');
insert into users(email, kind, first_name, last_name, password_hash) values ('toor', 'toor', 'Evil', 'Twin', '$2a$10$PE7xDOFE6./Mg81x62g61eAXXfHxMryMLXWq77Vm.XpEuLHMPRica');
insert into users(email, kind, first_name, last_name, password_hash) values ('dasja@test.shit.ua', 'admin', 'Даша', 'Босс', '$2a$10$Dt.OhdqCtSoF9chaj4uPZOi84AUfjSF6kQHaBLsrbG/XpEjELuEuK');


/* -------------------------------------------------------------------

drop table users;

create table test_foo(id bigserial);
create table test_bar(id bigserial);

select * from pg_trigger;
select * from information_schema.triggers;

select * from users;

delete from users where id = 8;

create table test_collation(id bigserial, name text);
insert into test_collation(name) values
    ('ввв'),
    ('ааа'),
    ('ббб'),
    ('ВВВ'),
    ('ААА'),
    ('БББ');
select * from test_collation order by name desc;

alter table test_collation owner to aps;

*/
