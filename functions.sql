-- id'si verilen bir urunun eldeki stok miktari ile total fiyatini hesaplayan fonksiyon
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

--buy_details'e ekleme yaparken urun adinin mevcut olup olmadigini kontrol eden trigger icin fonksiyon
create or replace function check_name_buy_details() returns trigger as $$
	begin
		if not exists (select 1 from product where product.name = NEW.product_name) then
			raise exception 'Product name does not exist.';
		end if;
		return NEW;
	end; $$
	language plpgsql;

-- bir person'in butun siparislerini detaylariyla listeleyen fonksiyon
create or replace function buy_details_of_person(p_id person.id%type) 
returns table(product_name varchar(20), quantity integer) as $$
	begin
		return query
		select buy_details.product_name, buy_details.quantity
		from buy_details, buy
		where buy_details.order_id = buy.order_id and buy.person_id = p_id;
	end; $$
language plpgsql;
