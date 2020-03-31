USE comp4111;

DROP TABLE IF EXISTS books;
CREATE TABLE books (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	title VARCHAR(255),
	author VARCHAR(255),
	publisher VARCHAR(255),
	year INT,
	available BOOLEAN NOT NULL DEFAULT 1,
	PRIMARY KEY (id),
	UNIQUE (title, author, publisher, year)
);

DROP TABLE IF EXISTS users;
CREATE TABLE users (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	username VARCHAR(255) NOT NULL UNIQUE,
	password VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
);

DROP PROCEDURE IF EXISTS init_user;
DELIMITER //
CREATE PROCEDURE init_user()
BEGIN
	DECLARE i INT DEFAULT 1;
	WHILE i <= 100 DO
		INSERT into users (username, password) VALUES (CONCAT('user', LPAD(i, 3, '0')), CONCAT('pass', LPAD(i, 3, '0')));
		SET i = i + 1;
	END WHILE;
END
//

DELIMITER ;

CALL init_user;
