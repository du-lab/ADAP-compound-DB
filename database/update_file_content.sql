CREATE TABLE `filecontent` (
  `Id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `content` longblob,
  `fileid` bigint unsigned NOT NULL,
  PRIMARY KEY (`Id`),
  KEY `filecontent_file_Id_fk` (`fileid`),
  CONSTRAINT `filecontent_file_Id_fk` FOREIGN KEY (`fileid`) REFERENCES `file` (`Id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;

-- update `file` set content = ( select content from `filecontent` where `filecontent`.fileid = `file`.id);

insert into `filecontent`(content, fileid) select content, id from `file`;