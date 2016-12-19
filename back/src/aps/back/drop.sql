drop table if exists user_roles;
drop table if exists user_tokens;
drop table if exists support_thread_messages;
drop table if exists support_threads;
drop table if exists ua_order_files;
drop table if exists ua_order_areas;
drop table if exists ua_orders;
drop table if exists file_user_permissions;
drop table if exists files;
drop table if exists users;

drop type if exists user_kind;
drop type if exists ua_document_type;
drop type if exists document_urgency;
drop type if exists ua_academic_level;
drop type if exists ua_order_state;

drop function if exists on_insert();
drop function if exists on_update();
drop function if exists users_tsv_trigger();
drop function if exists ua_orders_tsv_trigger();

