create table type_client
(
    id   serial primary key,
    external boolean not null,
    name varchar(128) not null
);
alter table type_client
    add constraint type_client_pk
        unique (name);

create table clients
(
    id    serial primary key,
    type_client_ref int references type_client(id),
    name  varchar(128)
);
alter table clients
    add constraint clients_pk
        unique (name);

create table measure_unit
(
    id   serial primary key,
    name varchar(25) not null
);
alter table measure_unit
    add constraint measure_unit_pk
        unique (name);

create table product
(
    id      serial primary key,
    unit_ref int,
    name    varchar(256)
);
create unique index product_pk
    on product (name, unit_ref);

alter table product
    add foreign key (unit_ref) references measure_unit;

create table documents --документы на приход/расход товара
(
    id          serial primary key,
    date        Date,
    numDoc      varchar(21),
    client_debet int references clients(id),
    client_kredit int references clients(id),
    product_ref  int references product(id),
    price       decimal(10,2),
    quantity    decimal(10,3)
);
create index documents_date_index
    on documents (date);

create table balance --сальдо по подразделениям
(
    id          serial primary key,
    date        date,
    client_ref   int references clients(id),
    product_ref  int references product(id),
    price       decimal(10,2),
    quantity_begin    decimal(10,3),
    quantity_coming   decimal(10,3),
    quantity_expense  decimal(10,3)
);

alter table balance
    add constraint balance_pk_2
        unique (date, client_ref, product_ref, price);
create index balance_client_ref_index
    on balance (client_ref);
create index balance_product_ref_index
    on balance (product_ref);

alter table balance
    add constraint balance_pk_3
        CHECK (coalesce(balance.quantity_begin,0)+coalesce(balance.quantity_coming,0)-coalesce(balance.quantity_expense,0)>=0);

create table price_list --прайслист
(
    id          serial primary key,
    date        date,
    product_ref  int references product(id),
    price       decimal(2)
);

alter table price_list
    add constraint price_list_pk
        unique (date, product_ref, price);

create table oper_day --опердень
(
    id          int,
    date        date
);

insert into oper_day(id,date)
values (1,to_date('01/04/2025','DD/MM/YYYY'));
select date from oper_day where id=1;

insert into documents
(date,  numDoc, client_debet, client_kredit, product_ref, price, quantity)
values (to_date('01/04/2025','DD/MM/YYYY'),'20250401/8',1,8,2,520.35,430.125);

insert into documents
(date,  numDoc, client_debet, client_kredit, product_ref, price, quantity)
values (to_date('01/04/2025','DD/MM/YYYY'),'20250401/8',1,8,1,120,50);

select d.id,d.date,d.numDoc,t.name vidd, c.name named,t2.name vidk, c2.name namek,p.name prod,d.quantity,m.name unit,d.price
from documents d
inner join clients c on c.id = d.client_debet
    inner join type_client t on t.id=c.type_client_ref
inner join clients c2 on c2.id = d.client_kredit
inner join type_client t2 on t2.id=c2.type_client_ref
inner join product p on p.id = d.product_ref
inner join measure_unit m on m.id=p.unit_ref
inner join oper_day od on d.date = od.date and od.id=1
;

insert into type_client(external, name)
values (true,'АО')
    insert into type_client(external, name)
values (true,'ТОО')
insert into type_client(external, name)
values (true,'ОАО')
insert into type_client(external, name)
values (false,'склад');
insert into type_client(external, name)
values (false,'магазин');

insert into clients(type_client_ref, name)
values (4,'Головной');
insert into clients(type_client_ref, name)
values (5,'Центральный');
insert into clients(type_client_ref, name)
values (5,'На Художников');
insert into clients(type_client_ref, name)
values (5,'На Энгельса');
insert into clients(type_client_ref, name)
values (5,'На Невском');

insert into clients (type_client_ref, name)
values (1,'Перекресток (мы)')
    insert into clients (type_client_ref, name)
values (2,'Молочный комбинат Ермолино');
insert into clients (type_client_ref, name)
values (3,'Молочный комбинат Пискаревский');
insert into clients (type_client_ref, name)
values (2,'Дикий фермер');
insert into clients (type_client_ref, name)
values (3,'Хлебный комбинат Урожайный');

insert into measure_unit(name)
values ('штук');
insert into measure_unit(name)
values ('кг');
insert into measure_unit(name)
values ('литров');
insert into measure_unit(name)
values ('пачек');

insert into product(name, unit_ref)
values ('печенье', 4);
insert into product(name, unit_ref)
values ('колбаса сервелат', 2);
insert into product(name, unit_ref)
values ('сосиски', 2);
insert into product(name, unit_ref)
values ('сардельки', 2);
insert into product(name, unit_ref)
values ('молоко пискаревсое 0.9', 1);
insert into product(name, unit_ref)
values ('пиво Жигулевское', 3);
insert into product(name, unit_ref)
values ('пиво Клинское', 3);
insert into product(name, unit_ref)
values ('кефир 0,5', 1);

select tc.id, tc.name from type_client tc--форма собственности внешних клиентов
where external;

select * from type_client -- виды подразделений предприятия АО "Перекресток"
where not external;

select cl.id as number, tС.name as type,cl.name as name from clients cl --клиенты
                                inner join type_client tС on tС.id = cl.type_client_ref and tС.external=true;

select cl.id as number,tС.name as type, cl.name as name from clients cl --подразделения
                                inner join type_client tС on tС.id = cl.type_client_ref and tС.external=false;

select * from measure_unit; --единицы измерений

select p.id, p.name, u.name --товар
from product p
         inner join measure_unit u on p.unit_ref = u.id
order by p.name, u.name;

select count(cl.id) as count--, cl.id as number, cl.name as name
from clients cl
         inner join type_client tС on tС.id = cl.type_client_ref and tС.external=true
where cl.id = 1
--group by cl.id, cl.name;


select d.id,d.date,d.numDoc,t.name vidd, c.name named,t2.name vidk, c2.name namek,p.name prod,d.quantity,m.name unit,d.price
from documents d
         inner join clients c on c.id = d.client_debet
         inner join type_client t on t.id=c.type_client_ref
         inner join clients c2 on c2.id = d.client_kredit
         inner join type_client t2 on t2.id=c2.type_client_ref
         inner join product p on p.id = d.product_ref
         inner join measure_unit m on m.id=p.unit_ref
         inner join oper_day od on d.date = od.date and od.id=1
;

/*
     id          serial primary key,
    date        date,
    client_ref   int references clients(id),
    product_ref  int references product(id),
    price       decimal(10,2),
    quantity_begin    decimal(10,3),
    quantity_coming   decimal(10,3),
    quantity_expense  decimal(10,3)
 */

insert into balance
(date,  client_ref, product_ref, price, quantity_coming)
values (to_date('01/04/2025','DD/MM/YYYY'),1,8,520.35,430.125);

update balance b
set quantity_coming = b.quantity_coming+200
    where b.id=1;

select d.id,d.date,tc.name vid, c.name name,p.name prod, d.price,coalesce(d.quantity_begin,0)+coalesce(d.quantity_coming,0)-coalesce(d.quantity_expense,0) as quantity_end,m.name unit,d.quantity_begin,d.quantity_coming,d.quantity_expense
from balance d
inner join clients c on c.id = d.client_ref
inner join type_client tc on c.type_client_ref = tc.id
inner join product p on p.id = d.product_ref
inner join measure_unit m on m.id=p.unit_ref
where d.client_ref=1;
select *
from clients;
select b.*
from balance b
where b.id=7
;
select *
from documents;
    insert into documents
(date,  numDoc, client_debet, client_kredit, product_ref, quantity, price)
select b.date,'xcvbxcb7',3,b.client_ref,b.product_ref,5,b.price
from balance b where b.id=7;

update balance b
set quantity_expense = coalesce(b.quantity_expense,0)+5
where b.id=7;
update balance b
set quantity_expense = b.quantity_expense+5
where b.id=7;

select d.id,d.date,d.numDoc,t.name vidd, c.name named,t2.name vidk, c2.name namek,p.name prod,d.quantity,m.name unit,d.price
from documents d
         inner join clients c on c.id = d.client_debet
         inner join type_client t on t.id=c.type_client_ref
         inner join clients c2 on c2.id = d.client_kredit
         inner join type_client t2 on t2.id=c2.type_client_ref
         inner join product p on p.id = d.product_ref
         inner join measure_unit m on m.id=p.unit_ref
         inner join oper_day od on d.date = od.date and od.id=1
;

truncate balance;
truncate documents;

insert into documents
(date,  numDoc, client_debet, client_kredit, product_ref, quantity, price)
select ?,?,?,?,b.product_ref,?,b.price
from balance b where b.id=?;

select d.id,d.date,tc.name vid, c.name name,p.name prod, d.price,coalesce(d.quantity_begin,0)+coalesce(d.quantity_coming,0)-coalesce(d.quantity_expense,0) as quantity_end,m.name unit,d.quantity_begin,d.quantity_coming,d.quantity_expense
from balance d
         inner join clients c on c.id = d.client_ref
         inner join type_client tc on c.type_client_ref = tc.id
         inner join product p on p.id = d.product_ref
         inner join measure_unit m on m.id=p.unit_ref
         inner join oper_day od on d.date = od.date and od.id=1
where d.client_ref=2;

select b.id
from balance d, balance b
where b.id=4 and d.date=b.date and d.client_ref=5
  and d.product_ref=b.product_ref and d.price=b.price;

insert into balance
(date,  client_ref, product_ref, price, quantity_coming)
select b.date,2,b.product_ref,b.price,3
from balance b where b.id=4;

select *
from balance

         update balance
set quantity_coming = coalesce(quantity_coming,0)+3
                where id=9;

select d.id
from balance d, balance b
where b.id=6 and d.date=b.date and d.client_ref=4
  and d.product_ref=b.product_ref and d.price=b.price;
