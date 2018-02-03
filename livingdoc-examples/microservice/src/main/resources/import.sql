insert into Customer(id,name) values ('aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa','Jimi Hendrix');
insert into Contract(id,idContract,effectDate,customer_id) values ('bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb','ct-12345','2017-01-10','aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa');
insert into CallAccess(id,customer_id,dateTime,phoneNumber,price,duration) values ('0000000000000000000000000000000000000001','aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa','2017-01-01T12:00:00','0791234567','2.3','PT43M');
insert into SmsAccess(id,customer_id,dateTime,phoneNumber,price) values ('0000000000000000000000000000000000000002','aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa','2017-11-14','0779874526','1.2');
insert into Bill(id,customerInformed,month,contract_id) values ('cccccccccccccccccccccccccccccccccccccccc','0','2017-11','bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb');
