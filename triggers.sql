-- TRIGGERLAR CALISIRKEN MESAJ DONECEK

-- buy_details'e ekleme yaparken urun adinin mevcut olup olmadigini kontrol eden trigger
create trigger check_name_buy_details_trigger
before insert on buy_details
for each row
execute function check_name_buy_details();

-- TODO id ve sifre icin kontrol trigger

-- TODO satin alim yaparken stok kontrolu yapan trigger