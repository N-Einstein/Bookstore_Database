CREATE DEFINER=`root`@`localhost` PROCEDURE `insertBooks`(no int)
BEGIN
declare i int;
set i = 0;
while(i < no) do
begin
INSERT INTO book VALUES (i, i, 'Arthur A. Levine Books', 'Art', 20.10, 2015, 'resources/images/bookCoverPhotos/Harry Potter and the Sorcerer\'s Stone.jpg' ,1);
set i = i + 1;
INSERT INTO book VALUES (i, i, 'Hachette Books', 'Art', 2.98, 2016, 'resources/images/bookCoverPhotos/American Pharaoh.jpg', 1);
set i = i + 1;
INSERT INTO stock VALUES(i-1, 15, 20, 1);
INSERT INTO stock VALUES(i-2, 15, 20, 1);
end;
end while;

END