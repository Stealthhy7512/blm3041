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

-- buy_details'e ekleme yaparken urun adinin mevcut olup olmadigini kontrol eden trigger icin fonksiyon
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

-- TODO function to compute grand total order cost for a person

		

-- TODO id ve sifre icin trigger icin fonksiyon
create or replace function login(p_id person.id%type, p_pass person.password%type)
returns boolean as $$
	begin
		if p_pass = (select password from person where id = p_id) then
			return true;
		else
			return false;
		end if;
	end; $$
language plpgsql;

-- TODO satin alim yaparken stok kontrolu yapan trigger icin fonksiyon
-- Kontrol et GPT yapti
create or replace function check_and_update_order_quantity()
returns trigger as $$
declare
    record RECORD;
begin
    for record in 
        select bd.product_name, bd.quantity, s.quantity as available_quantity
        from buy_details bd
        join storage s
          on bd.product_name = s.product_id
        where bd.order_id = NEW.order_id
    loop
        if record.quantity > record.available_quantity then
            raise exception 'Insufficient quantity for product: % (Requested: %, Available: %)', 
                            record.product_name, record.quantity, record.available_quantity;
        else
            update storage
            set quantity = quantity - record.quantity
            where product_id = record.product_name;
        end if;
    end loop;

    return NEW;
end;
$$ language plpgsql;
