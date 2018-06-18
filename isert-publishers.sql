CREATE DEFINER=`root`@`localhost` PROCEDURE `insertPublishers`()
BEGIN
declare i int;
declare c int;
set i = 0;
set c = 100;
while(i < c) do
begin
INSERT INTO publisher VALUES (i, '25-EL MAADY', '2255998', 1);
set i = i + 1;
end;
end while;
END