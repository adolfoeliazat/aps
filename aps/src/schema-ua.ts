/*
 * APS
 * 
 * (C) Copyright 2015-2016 Vladimir Grechka
 */

import {pgConnection, shutDownPool} from './backend'
#import static 'into-u/utils'

export async function createDB(newdb) {
    let condb
    if (newdb.startsWith('test-')) condb = 'test-postgres'
    else raise(`Can’t figure out condb for ${newdb}`)
    
    await shutDownPool(newdb)
    await pgConnection({db: condb}, async function(db) {
        await db.query({$tag: '67023702-2946-4c09-869c-a2cd49e0c857', y: `drop database if exists "${newdb}"`})
        await db.query({$tag: '50efc291-72eb-4b06-b132-8270e1180b20', y: `create database "${newdb}"`})
    })
    await pgConnection({db: newdb}, async function(db) {
        await db.query({$tag: 'd891345e-3287-43b0-b6fc-7174fb9d2cd3', y: `
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
                kind text not null,
                lang text not null,
                email text unique not null,
                password_hash text not null,
                state text not null,
                assigned_to bigint /*can be null*/ references users(id),
                admin_notes text /*can be null*/,
                first_name text not null,
                last_name text not null,
                phone text, /*can be null*/
                about_me text /*can be null*/
            );
            alter sequence users_id_seq restart with 100000;
            create trigger on_insert before insert on users for each row execute procedure on_insert();
            create trigger on_update before update on users for each row execute procedure on_update();
            
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
                supporter_id bigint /*can be null*/ references users(id),
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
                recipient_id bigint /*can be null*/ references users(id),
                message text not null,
                data jsonb not null
                );
            alter sequence support_thread_messages_id_seq restart with 100000;
            create trigger on_insert before insert on support_thread_messages for each row execute procedure on_insert();
            create trigger on_update before update on support_thread_messages for each row execute procedure on_update();
            
            
        `})
    })
}
