/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

drop function if exists onInsert();
create or replace function onInsert()
returns trigger as $$
begin
    new.deleted = false;
    new.insertedAt = now() at time zone 'utc';
    new.updatedAt = new.insertedAt;
    return new;	
end;
$$ language 'plpgsql';


drop function if exists onUpdate();
create or replace function onUpdate()
returns trigger as $$
begin
    new.updatedAt = now() at time zone 'utc';
    return new;	
end;
$$ language 'plpgsql';


create table users(
    id bigserial,
    deleted boolean,
    insertedAt timestamp,
    updatedAt timestamp,
    email text unique,
    hash text,
    state text,
    firstName text,
    lastName text);
create trigger onInsert before insert on users for each row execute procedure onInsert();
create trigger onUpdate before update on users for each row execute procedure onUpdate();

insert into users(email, hash) values ('root', '$2a$10$bWP5kkNWANH3S2C4c0hgbuhR1uZBXiW84OMzcoTvY559e8azTcXcK');
update users set firstName = 'Vladimir', lastName = 'Grechka' where email = 'root';
insert into users(email, hash) values ('toor', '$2a$10$PE7xDOFE6./Mg81x62g61eAXXfHxMryMLXWq77Vm.XpEuLHMPRica');
update users set firstName = 'Evil', lastName = 'Twin' where email = 'toor';



/* -------------------------------------------------------------------

create table test_foo(id bigserial);
create table test_bar(id bigserial);

select * from pg_trigger;
select * from information_schema.triggers;

select * from users;

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
