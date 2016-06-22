create table test_collation(id bigserial, name text);
insert into test_collation(name) values;
    ('ввв'),
    ('ааа'),
    ('ббб'),
    ('ВВВ'),
    ('ААА'),
    ('БББ');
select * from test_collation order by name desc;


