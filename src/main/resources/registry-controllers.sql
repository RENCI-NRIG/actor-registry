USE ActorRegistry;

DROP TABLE IF EXISTS `Controllers`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `Controllers` (
  `ctrl_id` int(11) NOT NULL auto_increment,
  `ctrl_name` varchar(255) NOT NULL,
  `ctrl_url` varchar(255) NOT NULL,
  `ctrl_description` text,
  `ctrl_enabled` boolean,
  PRIMARY KEY(`ctrl_id`),
  UNIQUE KEY `ctrl_url` (`ctrl_url`)
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;
