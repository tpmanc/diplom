INSERT INTO property VALUES ('11', 'Разрядность');

CREATE TABLE fileTrigger (
  id int(11) NOT NULL AUTO_INCREMENT,
  extension varchar(10) NOT NULL,
  command text NOT NULL,
  `regexp` text NOT NULL,
  PRIMARY KEY (id)
) AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;