-- en az bir appointmenti olan insanlari, appointment sayisiyla birlikte listeleyen view
create or replace view display_people_with_appointments as
	select person.fname, person.lname, count(*)
	from person, appointment
	where person.id = appointment.owner_id
	group by person.id
	having count(*) > 0;

-- randevusu olmayan hayvanlari listeleyen view
create or replace view display_pets_without_appointments as
	select pet.name
	from pet
	except (
		select pet.name
		from pet, appointment
		where pet.id = appointment.pet_id
		order by pet.name); 

-- TODO belki appointmenti olan hayvanlar
create or replace view display_pets_with_appointments as
	select pet.name
	from pet, appointment
	where pet.id = appointment.pet_id