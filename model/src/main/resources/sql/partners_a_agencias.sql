insert into agency (
id
, pvpallowed
, comments
, email
, fulladdress
, name
, onrequestallowed
, onelineperbooking
, shuttletransfersinowninvoice
, status
, telephone
, thridpartyallowed
, cancellationrules_id
, company_id
, currency_isocode
, financialagent_id
, group_id
, handlingfee_id
, market_id
, markup_id
) select
id
, pvpallowed
, comments
, email
, fulladdress
, name
, onrequestallowed
, onelineperbooking
, shuttletransfersinowninvoice
, status
, telephone
, thridpartyallowed
, cancellationrules_id
, 1
, currency_isocode
, financialagent_id
, group_id
, handlingfee_id
, market_id
, markup_id
from partner where agency;



insert into provider (
id
, automaticorderconfirmation
, automaticordersending
, comments
, email
, extramarkuppercent
, fulladdress
, name
, orderssendingmethod
, payablebyinvoucher
, sendordersto
, status
, telephone
, currency_isocode
, financialagent_id
) select
id
, automaticorderconfirmation
, automaticordersending
, comments
, email
, extramarkuppercent
, fulladdress
, name
, orderssendingmethod
, payablebyinvoucher
, sendordersto
, status
, telephone
, currency_isocode
, financialagent_id
from partner where provider;



select max(id) from partner;

alter sequence agency_id_seq restart with 58;

alter sequence provider_id_seq restart with 58;

alter table booking alter column overridedvalue type double precision using totalvalue;

--update booking set overridedvalue = totalvalue where valueoverrided;

update charge set currency_isocode = 'EUR', currencyexchange = 1, nucs = total_value, total = total_value, agency_id = partner_id;

update purchaseorder set currency_isocode = 'EUR', total = value_value;

update service set totalsale = total;

alter table booking alter column overridedcost type double precision using totalcost;

update booking set overridedcost = 28.38, overridedcostcurrency_isocode = 'EUR' where id = 12290;

update currency set exchangeratetonucs = 1 where isocode = 'EUR';

update booking set valueinnucs = totalnetvalue, costinnucs = totalcost, totalmarkup = totalnetvalue - totalcost;


