DROP TABLE IF EXISTS `domain`;
CREATE TABLE IF NOT EXISTS `domain` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `parent` bigint(20) unsigned DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `uuid` varchar(40) DEFAULT NULL,
  `owner` bigint(20) unsigned NOT NULL,
  `path` varchar(255) NOT NULL,
  `level` int(10) NOT NULL DEFAULT '0',
  `child_count` int(10) NOT NULL DEFAULT '0',
  `next_child_seq` bigint(20) unsigned NOT NULL DEFAULT '1',
  `removed` datetime DEFAULT NULL,
  `state` char(32) NOT NULL DEFAULT 'Active',
  `network_domain` varchar(255) DEFAULT NULL,
  `type` varchar(255) NOT NULL DEFAULT 'Normal',
  PRIMARY KEY (`id`),
  UNIQUE KEY `parent` (`parent`,`name`,`removed`),
  UNIQUE KEY `uc_domain__uuid` (`uuid`),
  KEY `i_domain__path` (`path`),
  KEY `i_domain__removed` (`removed`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
