NO WAY

drop table foobar;
create table foobar(id bigserial, foo text, bar text, data jsonb);
insert into foobar (foo, bar, data) values ('aaa', 'bbb', '{"seenBy": {"10": "t1", "20": "t2"}}');

update foobar set data = data || jsonb_build_object('seenBy', data->'seenBy' || '{"20": "t51"}') where id = 1

select * from foobar

select '{"seenBy": {"10": "t1", "20": "t2"}}'::jsonb || '{"20": "t5"}'::jsonb

