CREATE TABLE category (
  id int(11) NOT NULL AUTO_INCREMENT,
  parent int(11) NOT NULL,
  title varchar(255) NOT NULL,
  position int(11) NOT NULL,
  PRIMARY KEY (id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE exportTemplate (
  id int(11) NOT NULL AUTO_INCREMENT,
  title varchar(255) DEFAULT NULL,
  parameters text,
  finalCommands text,
  finalCommandsInterpreter int(5) NOT NULL,
  PRIMARY KEY (id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE file (
  id int(11) NOT NULL AUTO_INCREMENT,
  title varchar(255) NOT NULL,
  PRIMARY KEY (id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE fileCategory (
  id int(11) NOT NULL AUTO_INCREMENT,
  fileId int(11) NOT NULL,
  categoryId int(11) NOT NULL,
  PRIMARY KEY (id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE fileProperty (
  id int(11) NOT NULL AUTO_INCREMENT,
  fileId int(11) NOT NULL,
  propertyId int(11) NOT NULL,
  value varchar(255) NOT NULL,
  PRIMARY KEY (id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE fileVersion (
  id int(11) NOT NULL AUTO_INCREMENT,
  fileId int(11) NOT NULL,
  userId int(11) NOT NULL,
  version varchar(255) NOT NULL,
  hash varchar(32) NOT NULL,
  fileSize bigint(20) NOT NULL,
  date bigint(11) NOT NULL,
  isFilled int(1) NOT NULL,
  fileName varchar(255) NOT NULL,
  isDisabled int(1) NOT NULL,
  PRIMARY KEY (id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE fileVersionProperty (
  id int(11) NOT NULL AUTO_INCREMENT,
  fileVersionId int(11) NOT NULL,
  propertyId int(11) NOT NULL,
  value varchar(255) NOT NULL,
  PRIMARY KEY (id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE property (
  id int(11) NOT NULL AUTO_INCREMENT,
  title varchar(255) NOT NULL,
  PRIMARY KEY (id)
) AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

INSERT INTO property VALUES ('1', 'Описание файла');
INSERT INTO property VALUES ('2', 'Исходное имя файла');
INSERT INTO property VALUES ('3', 'Версия файла');
INSERT INTO property VALUES ('4', 'Версия продукта');
INSERT INTO property VALUES ('5', 'Авторские права');
INSERT INTO property VALUES ('6', 'Автор');
INSERT INTO property VALUES ('7', 'Товарные знаки');
INSERT INTO property VALUES ('8', 'Внутреннее имя');
INSERT INTO property VALUES ('9', 'Название продукта');
INSERT INTO property VALUES ('10', 'Комментарий');

CREATE TABLE request (
  id int(11) NOT NULL AUTO_INCREMENT,
  userId int(11) NOT NULL,
  text text NOT NULL,
  status int(1) NOT NULL,
  date bigint(11) NOT NULL,
  comment text NOT NULL,
  PRIMARY KEY (id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE requestFile (
  id int(11) NOT NULL AUTO_INCREMENT,
  requestId int(11) NOT NULL,
  hash varchar(32) NOT NULL,
  fileName varchar(255) NOT NULL,
  fileSize bigint(20) NOT NULL,
  extension varchar(10) NOT NULL,
  PRIMARY KEY (id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE settings (
  id int(11) NOT NULL AUTO_INCREMENT,
  value text,
  PRIMARY KEY (id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE user (
  id int(11) NOT NULL,
  phone varchar(255) DEFAULT NULL,
  email varchar(255) DEFAULT NULL,
  displayName varchar(500) DEFAULT NULL,
  department varchar(500) DEFAULT NULL,
  departmentNumber varchar(10) DEFAULT NULL,
  address text,
  PRIMARY KEY (id)
) DEFAULT CHARSET=utf8;