USE ActorRegistry;

DROP TABLE IF EXISTS `Images`;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
CREATE TABLE `Images` (
  `img_id` int(11) NOT NULL auto_increment,
  `img_simple_name` varchar(255) NOT NULL,
  `img_ver` varchar(255) NOT NULL,
  `img_neuca_ver` varchar(255) NOT NULL,
  `img_url` varchar(255) NOT NULL,
  `img_hash` varchar(255) NOT NULL,
  `img_owner` varchar(255) NOT NULL,
  `img_description` text,
  `img_date` datetime,
  `img_default` boolean,
  PRIMARY KEY(`img_id`),
  UNIQUE KEY `img_hash` (`img_hash`)
) ENGINE=InnoDB AUTO_INCREMENT=175 DEFAULT CHARSET=utf8;
SET character_set_client = @saved_cs_client;
