/*
 * APS
 *
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

create function on_insert()
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

create function on_update()
returns trigger as $$
begin
    if new.updated_at is null then
        new.updated_at = now() at time zone 'utc';
    end if;
    return new;
end;
$$ language 'plpgsql';

-- @ctx tables

create table users(
    id bigserial primary key,
    deleted boolean not null,
    inserted_at timestamp not null,
    updated_at timestamp not null,
    profile_updated_at timestamp,
    tsv tsvector not null,
    kind text not null,
    lang text not null,
    email text unique not null,
    password_hash text not null,
    state text not null,
    profile_rejection_reason text /*maybe null*/,
    ban_reason text /*maybe null*/,
    assigned_to bigint /*maybe null*/ references users(id),
    admin_notes text not null,
    first_name text not null,
    last_name text not null,
    phone text /*maybe null*/,
    compact_phone text /*maybe null*/,
    about_me text /*maybe null*/
);

alter sequence users_id_seq restart with 100000;
create trigger on_insert before insert on users for each row execute procedure on_insert();
create trigger on_update before update on users for each row execute procedure on_update();
create index tsv_idx on users using gin (tsv);

create function users_tsv_trigger() returns trigger as $$
begin
  new.tsv :=
     setweight(to_tsvector('pg_catalog.russian', ' '
         ||' '|| coalesce(new.email,'')
         ||' '|| coalesce(new.first_name,'')
         ||' '|| coalesce(new.last_name,'')
         ),'A')
     ||
     setweight(to_tsvector('pg_catalog.russian', ' '
         ||' '|| coalesce(new.admin_notes,'')
         ),'B')
     ||
     setweight(to_tsvector('pg_catalog.russian', ' '
         ||' '|| coalesce(new.phone,'')
         ||' '|| coalesce(new.about_me,'')
         ),'D')
  ;
  return new;
end
$$ language plpgsql;

create trigger users_tsvectorupdate before insert or update
    on users for each row execute procedure users_tsv_trigger();



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
    tslang text not null,
    topic text not null,
    supportee_id bigint not null references users(id),
    supporter_id bigint /*maybe null*/ references users(id),
    status text not null
    );
alter sequence support_threads_id_seq restart with 100000;
create trigger on_insert before insert on support_threads for each row execute procedure on_insert();
create trigger on_update before update on support_threads for each row execute procedure on_update();

create table support_thread_messages(
    id bigserial primary key,
    deleted boolean not null,
    inserted_at timestamp not null,
    updated_at timestamp not null,
    tslang text not null,
    thread_id bigint not null references support_threads(id),
    sender_id bigint not null references users(id),
    recipient_id bigint /*maybe null*/ references users(id),
    message text not null,
    data jsonb not null
    );
alter sequence support_thread_messages_id_seq restart with 100000;
create trigger on_insert before insert on support_thread_messages for each row execute procedure on_insert();
create trigger on_update before update on support_thread_messages for each row execute procedure on_update();

-- create table foobar(id bigserial primary key, foo text);


-- ============================== UA_ORDERS ==============================


create type ua_document_type as enum (
    'ESSAY', -- Реферат
    'COURSE', -- Курсовая
    'GRADUATION' -- Дипломная
);

create type document_urgency as enum (
    'H12', 'H24', 'D3', 'D5', 'D7', 'D8'
);

create type ua_academic_level as enum (
    'SCHOOL',
    'INSTITUTE'
);

create type ua_order_state as enum (
    'CREATED',
    'LOOKING_FOR_WRITERS',
    'WAITING_FOR_PAYMENT',
    'WRITER_ASSIGNED'
);

create table ua_orders(
    id bigserial primary key,
    deleted boolean not null,
    inserted_at timestamp not null,
    updated_at timestamp not null,
    tsv tsvector not null,
    creator_id bigint not null references users(id), -- Can be admin
    customer_id bigint not null references users(id),
    title text not null,
    document_type ua_document_type not null,
    deadline timestamp not null,
    price int /*maybe null*/,
    num_pages int not null,
    num_sources int not null,
    details text not null,
    admin_notes text not null,
    state ua_order_state not null,
    writer_id bigint /*maybe null*/ references users(id)
);

alter sequence ua_orders_id_seq restart with 100000;
create trigger on_insert before insert on ua_orders for each row execute procedure on_insert();
create trigger on_update before update on ua_orders for each row execute procedure on_update();
create index ua_orders_tsv_idx on ua_orders using gin (tsv);

create function ua_orders_tsv_trigger() returns trigger as $$
begin
  new.tsv :=
     setweight(to_tsvector('pg_catalog.russian', ' '
         ||' '|| coalesce(new.title,'')
         ),'A')
     ||
     setweight(to_tsvector('pg_catalog.russian', ' '
         ||' '|| coalesce(new.details,'')
         ||' '|| coalesce(new.admin_notes,'')
         ),'B')
  ;
  return new;
end
$$ language plpgsql;

create trigger ua_orders_tsvectorupdate before insert or update
    on ua_orders for each row execute procedure ua_orders_tsv_trigger();

-- ============================== FILES ==============================

create table files(
    id bigserial primary key,
    deleted boolean not null,
    inserted_at timestamp not null,
    updated_at timestamp not null,
    creator_id bigint not null references users(id),
    content bytea not null,
    name text not null,
    title text not null,
    mime text not null,
    size_bytes int not null,
    details text not null,
    admin_notes text not null
);

alter sequence files_id_seq restart with 100000;
create trigger on_insert before insert on files for each row execute procedure on_insert();
create trigger on_update before update on files for each row execute procedure on_update();


-- ============================== FILE_USER_PERMISSIONS ==============================

create table file_user_permissions(
    file_id bigserial not null references files(id),
    user_id bigserial not null references users(id),
    primary key (file_id, user_id),
    deleted boolean not null,
    inserted_at timestamp not null,
    updated_at timestamp not null
);

create trigger on_insert before insert on file_user_permissions for each row execute procedure on_insert();
create trigger on_update before update on file_user_permissions for each row execute procedure on_update();





