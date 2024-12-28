create table pet (
	id integer primary key,
	name varchar(20) not null,
	species varchar(20) not null,
	breed varchar(20),
	age integer check (age between 0 and 30),
	sex char(1)
);

create table person (
	id integer primary key,
	fname varchar(20) not null,
	lname varchar(20) not null,
	phone_number char(10)
);

create table pets (
	owner_id integer,
	pet_id integer,

	primary key (owner_id, pet_id),
	foreign key (owner_id) references person(id),
	foreign key (pet_id) references pet(id)
);

create table appointment (
	appointment_id integer primary key,
	owner_id integer not null,
	pet_id integer not null,
	date varchar(10) not null,

	foreign key (owner_id) references person(id),
	foreign key (pet_id) references pet(id)	
);

create table product (
	id integer primary key,
	name varchar(20) not null,
	cost integer not null
);

create table storage (
	product_id integer primary key,
	quantity integer,

	foreign key (product_id) references product(id)
);

create table buy (
	order_id integer primary key,
	person_id integer not null,

	foreign key (person_id) references person(id)
);

create table buy_details (
	order_id primary key,
	product_name not null,
	quantity integer not null,

	foreign key (product_name) references product(name)
);

create sequence person_seq
	start with 1
	increment by 1;

create sequence pet_seq
	start with 1
	increment by 1;

create sequence appointment_seq
	start with 1
	increment by 1;

create or replace function compute_cost(id_prod product.id%type) returns integer as $$
	declare 
		total integer := 0;
	begin
		select quantity * cost into total
		from product, storage
		where product.id = id_prod and storage.product_id = id_prod;

		return total;
	end; $$
	language plpgsql;

create or replace view display_appointments as
	select person.fname, person.lname, pet.name, appointment.date 
	from person, appointment, pets, pet
	where person.id = appointment.owner_id and pets.owner_id = person.id and pets.pet_id = pet.id  
	order by person.id;

alter table appointments rename to appointment;

insert into person values (nextval('person_seq'), 'kaan', 'yazici', '5533647175');
insert into pet values (nextval('pet_seq'), 'zeytin', 'dog', 'terrier', 6, 'M');
insert into pets values (1, 1);
insert into appointment values (nextval('appointment_seq'), 1, 1, '28/12/2024');	

create or replace view display_people_with_appointments as
	select person.fname, person.lname, count(*)
	from person, appointment
	where person.id = appointment.owner_id
	group by person.id
	having count(*) > 0;

select * from display_people_with_appointments;
select * from person;
insert into person values (nextval('person_seq'), 'mert', 'yazici', '5521231234');

	

