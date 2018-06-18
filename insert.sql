USE bookstore_database;
SET FOREIGN_KEY_CHECKS=0;
INSERT INTO author VALUES ('J. K. Rowling', '9780545790352', '1');
INSERT INTO author VALUES ('Joe Drape', '9780316268844', '1');
SET FOREIGN_KEY_CHECKS=1;
INSERT INTO publisher VALUES ('Arthur A. Levine Books', '25-EL MAADY', '2255998', 1);
INSERT INTO publisher VALUES ('Hachette Books', 10, 'tele', 1);

INSERT INTO book VALUES ('9780545790352', 'Harry Potter and the Sorcerer\'s Stone: The Illustrated Edition (Harry Potter, Book 1)', 'Arthur A. Levine Books', 'Art', 20.10, 2015, 'resources/images/bookCoverPhotos/Harry Potter and the Sorcerer\'s Stone.jpg' ,1);
INSERT INTO book VALUES ('970316268844', 'American Pharaoh: The Untold Story of the Triple Crown Winner\'s Legendary Rise', 'Hachette Books', 'Art', 2.98, 2016, 'resources/images/bookCoverPhotos/American Pharaoh.jpg', 1);

INSERT INTO stock VALUES('9780545790352', 15, 20, 1);
INSERT INTO stock VALUES('970316268844', 15, 18, 1);

INSERT INTO user values (1, 'aya@k.com', 'aya', 'sameh', '202cb962ac59075b964b07152d234b70', 1, 'a', '1', 'aya', 1);
INSERT INTO user values (2, 'y@k.com', 'yasmin', 'barakat', '202cb962ac59075b964b07152d234b70', 2, 'a', '1', 'yasmin', 1);

call insertBooks(100);
call insertPublishers();



/*
DELIMITER $$
create event delete_eventww
on schedule every 30 second
starts now()
do 
begin 
SET SQL_SAFE_UPDATES = 0;
update users_orders set valid = 0;
SET SQL_SAFE_UPDATES = 1;
end;
$$
*/

/*call insert_users();*/





/* 5513105225648142 */

/*
9780545790352
9780306406157
9780694003617
9781234567897
9781566199094

*/


