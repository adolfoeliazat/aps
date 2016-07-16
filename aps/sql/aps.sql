/*
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
    if new.inserted_at is null then
        new.inserted_at = now() at time zone 'utc';
    end if;
    if new.updated_at is null then
        new.updated_at = new.inserted_at;
    end if;
    return new;	
end;
$$ language 'plpgsql';


drop function if exists on_update();
create or replace function on_update()
returns trigger as $$
begin
    if new.updated_at is null then
        new.updated_at = now() at time zone 'utc';
    end if;
    return new;	
end;
$$ language 'plpgsql';

create table users(
    id bigserial primary key,
    deleted boolean not null,
    inserted_at timestamp not null,
    updated_at timestamp not null,
    profile_updated_at timestamp,
    kind text not null,
    lang text not null,
    email text unique not null,
    password_hash text not null,
    state text not null,
    first_name text not null,
    last_name text not null,
    phone text /*can be null*/);
alter sequence users_id_seq restart with 100000;
create trigger on_insert before insert on users for each row execute procedure on_insert();
create trigger on_update before update on users for each row execute procedure on_update();

-- insert into users (id, email, kind, lang, first_name, last_name, state, password_hash) values (10, 'root', 'root', 'ua', 'Vladimir', 'Grechka', 'cool', '$2a$10$bWP5kkNWANH3S2C4c0hgbuhR1uZBXiW84OMzcoTvY559e8azTcXcK');
-- insert into users (id, email, kind, lang, first_name, last_name, state, password_hash) values (11, 'toor', 'toor', 'ua', 'Evil', 'Twin', 'cool', '$2a$10$PE7xDOFE6./Mg81x62g61eAXXfHxMryMLXWq77Vm.XpEuLHMPRica');
-- insert into users (id, email, kind, lang, first_name, last_name, state, password_hash) values (12, 'dasja@test.shit.ua', 'admin', 'ua', 'Даша', 'Босс', 'cool', '$2a$10$Dt.OhdqCtSoF9chaj4uPZOi84AUfjSF6kQHaBLsrbG/XpEjELuEuK');

create table user_roles(
    id bigserial primary key,
    deleted boolean not null,
    inserted_at timestamp not null,
    updated_at timestamp not null,
    user_id bigint not null references users(id),
    role text not null
);
create trigger on_insert before insert on user_roles for each row execute procedure on_insert();
create trigger on_update before update on user_roles for each row execute procedure on_update();
alter table user_roles add constraint unique_user_id_role unique (user_id, role);


create table user_tokens(
    id bigserial primary key,
    deleted boolean,
    inserted_at timestamp,
    updated_at timestamp,
    user_id bigint references users(id),
    token text
    );
create trigger on_insert before insert on user_tokens for each row execute procedure on_insert();
create trigger on_update before update on user_tokens for each row execute procedure on_update();


create table support_threads(
    id bigserial primary key,
    deleted boolean not null,
    inserted_at timestamp not null,
    updated_at timestamp not null,
    topic text not null,
    supportee_id bigint not null references users(id),
    supporter_id bigint /*can be null*/ references users(id)
    );
alter sequence support_threads_id_seq restart with 100000;    
create trigger on_insert before insert on support_threads for each row execute procedure on_insert();
create trigger on_update before update on support_threads for each row execute procedure on_update();


create table support_thread_messages(
    id bigserial primary key,
    deleted boolean not null,
    inserted_at timestamp not null,
    updated_at timestamp not null,
    thread_id bigint not null references support_threads(id),
    sender_id bigint not null references users(id),
    recipient_id bigint /*can be null*/ references users(id),
    message text not null
    );
alter sequence support_thread_messages_id_seq restart with 100000;
create trigger on_insert before insert on support_thread_messages for each row execute procedure on_insert();
create trigger on_update before update on support_thread_messages for each row execute procedure on_update();


create table foobar(id bigserial, foo text);

/* -------------------------------------------------------------------

SELECT * FROM pg_stat_activity


select * from foobar

select * from users
select * from user_tokens
select * from support_threads
select * from support_thread_messages


select * from users where id in (2, 78)

select * from support_thread_messages t1,
              users t2,
              users t3
         where support_thread_messages.sender_id = users.id

select * from support_threads
select * from support_thread_messages



select * from foobar
delete from foobar
insert into foobar(foo) values('qqqqq') returning id

select * from users;
select * from user_tokens;

select * from support_messages
delete from support_messages
drop table support_messages

select id from users where email = 'fred.red@test.shit.ua'

select * from users, user_tokens
where user_tokens.token = '45550a30-9124-4f9d-9219-204af0737b21' and
      users.id = user_tokens.user_id


drop table user_tokens;
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

select now() at time zone 'utc'

select users.id user_id, * from users, user_tokens

select typname, oid, typarray from pg_type order by oid

select typname, oid, typarray from pg_type where typname = 'daterange' order by oid

select version()

select * from foobar

insert into foobar(id, foo) values(null, 'qqqqqqq')

insert into foobar (foo) values ('qwuqwu')

delete from support_threads

-- Drop all shit

drop table if exists support_thread_messages;
drop table if exists support_threads;
drop table if exists user_tokens;
drop table if exists user_roles;
drop table if exists users;
drop table if exists foobar;

*/
