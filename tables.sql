create table pet (
	id integer primary key,
	name varchar(20) not null,
	species varchar(20) not null,
	breed varchar(20),
	age integer check (age between 0 and 30),
	sex char(1)
);

-- dropped phone number
create table person (
	id integer primary key,
	fname varchar(20) not null,
	lname varchar(20) not null
);

-- primary key only pet_id
create table pets (
	owner_id integer,
	pet_id integer,

	primary key (pet_id),
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
	order_id integer primary key,
	product_name varchar(20) not null,
	quantity integer not null,

	foreign key (order_id) references buy(order_id)
);