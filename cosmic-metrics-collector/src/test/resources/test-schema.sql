DROP TABLE IF EXISTS `vm_instance`;
CREATE TABLE IF NOT EXISTS `vm_instance` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `uuid` varchar(40) DEFAULT NULL,
  `instance_name` varchar(255) NOT NULL,
  `state` varchar(32) NOT NULL,
  `vm_template_id` bigint(20) unsigned DEFAULT NULL,
  `guest_os_id` bigint(20) unsigned NOT NULL,
  `private_mac_address` varchar(17) DEFAULT NULL,
  `private_ip_address` char(40) DEFAULT NULL,
  `pod_id` bigint(20) unsigned DEFAULT NULL,
  `data_center_id` bigint(20) unsigned NOT NULL,
  `host_id` bigint(20) unsigned DEFAULT NULL,
  `last_host_id` bigint(20) unsigned DEFAULT NULL,
  `proxy_id` bigint(20) unsigned DEFAULT NULL,
  `proxy_assign_time` datetime DEFAULT NULL,
  `vnc_password` varchar(255) NOT NULL,
  `ha_enabled` tinyint(1) NOT NULL DEFAULT '0',
  `limit_cpu_use` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `update_count` bigint(20) unsigned NOT NULL DEFAULT '0',
  `update_time` datetime DEFAULT NULL,
  `created` datetime NOT NULL,
  `removed` datetime DEFAULT NULL,
  `type` varchar(32) NOT NULL,
  `vm_type` varchar(32) NOT NULL,
  `account_id` bigint(20) unsigned NOT NULL,
  `domain_id` bigint(20) unsigned NOT NULL,
  `service_offering_id` bigint(20) unsigned NOT NULL,
  `reservation_id` char(40) DEFAULT NULL,
  `hypervisor_type` char(32) DEFAULT NULL,
  `disk_offering_id` bigint(20) unsigned DEFAULT NULL,
  `owner` varchar(255) DEFAULT NULL,
  `host_name` varchar(255) DEFAULT NULL,
  `display_name` varchar(255) DEFAULT NULL,
  `desired_state` varchar(32) DEFAULT NULL,
  `dynamically_scalable` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `display_vm` tinyint(1) NOT NULL DEFAULT '1',
  `power_state` varchar(74) DEFAULT 'PowerUnknown',
  `power_state_update_time` datetime DEFAULT NULL,
  `power_state_update_count` int(11) DEFAULT '0',
  `power_host` bigint(20) unsigned DEFAULT NULL,
  `user_id` bigint(20) unsigned NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uc_vm_instance_uuid` (`uuid`),
  KEY `i_vm_instance__removed` (`removed`),
  KEY `i_vm_instance__type` (`type`),
  KEY `i_vm_instance__pod_id` (`pod_id`),
  KEY `i_vm_instance__update_time` (`update_time`),
  KEY `i_vm_instance__update_count` (`update_count`),
  KEY `i_vm_instance__state` (`state`),
  KEY `i_vm_instance__data_center_id` (`data_center_id`),
  KEY `fk_vm_instance__host_id` (`host_id`),
  KEY `fk_vm_instance__last_host_id` (`last_host_id`),
  KEY `i_vm_instance__template_id` (`vm_template_id`),
  KEY `fk_vm_instance__account_id` (`account_id`),
  KEY `fk_vm_instance__service_offering_id` (`service_offering_id`),
  KEY `fk_vm_instance__power_host` (`power_host`),
  KEY `i_vm_instance__instance_name` (`instance_name`)
--  CONSTRAINT `fk_vm_instance__account_id` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`),
--  CONSTRAINT `fk_vm_instance__host_id` FOREIGN KEY (`host_id`) REFERENCES `host` (`id`),
--  CONSTRAINT `fk_vm_instance__last_host_id` FOREIGN KEY (`last_host_id`) REFERENCES `host` (`id`),
--  CONSTRAINT `fk_vm_instance__power_host` FOREIGN KEY (`power_host`) REFERENCES `host` (`id`),
--  CONSTRAINT `fk_vm_instance__service_offering_id` FOREIGN KEY (`service_offering_id`) REFERENCES `service_offering` (`id`),
--  CONSTRAINT `fk_vm_instance__template_id` FOREIGN KEY (`vm_template_id`) REFERENCES `vm_template` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

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

DROP TABLE IF EXISTS `account`;
CREATE TABLE IF NOT EXISTS `account` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `account_name` varchar(100) DEFAULT NULL,
  `uuid` varchar(40) DEFAULT NULL,
  `type` int(1) unsigned NOT NULL,
  `domain_id` bigint(20) unsigned DEFAULT NULL,
  `state` varchar(10) NOT NULL DEFAULT 'enabled',
  `removed` datetime DEFAULT NULL,
  `cleanup_needed` tinyint(1) NOT NULL DEFAULT '0',
  `network_domain` varchar(255) DEFAULT NULL,
  `default_zone_id` bigint(20) unsigned DEFAULT NULL,
  `default` int(1) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uc_account__uuid` (`uuid`),
  KEY `i_account__removed` (`removed`),
  KEY `fk_account__default_zone_id` (`default_zone_id`),
  KEY `i_account__cleanup_needed` (`cleanup_needed`),
  KEY `i_account__account_name__domain_id__removed` (`account_name`,`domain_id`,`removed`),
  KEY `i_account__domain_id` (`domain_id`)
--  CONSTRAINT `fk_account__default_zone_id` FOREIGN KEY (`default_zone_id`) REFERENCES `data_center` (`id`) ON DELETE CASCADE,
--  CONSTRAINT `fk_account__domain_id` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `service_offering`;
CREATE TABLE IF NOT EXISTS `service_offering` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `cpu` int(10) unsigned DEFAULT NULL,
  `speed` int(10) unsigned DEFAULT NULL,
  `ram_size` bigint(20) unsigned DEFAULT NULL,
  `nw_rate` smallint(5) unsigned DEFAULT '200',
  `mc_rate` smallint(5) unsigned DEFAULT '10',
  `ha_enabled` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `limit_cpu_use` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `host_tag` varchar(255) DEFAULT NULL,
  `default_use` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `vm_type` varchar(32) DEFAULT NULL,
  `sort_key` int(32) NOT NULL DEFAULT '0',
  `is_volatile` tinyint(1) unsigned NOT NULL DEFAULT '0',
  `deployment_planner` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
--  CONSTRAINT `fk_service_offering__id` FOREIGN KEY (`id`) REFERENCES `disk_offering` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `user_ip_address`;
CREATE TABLE IF NOT EXISTS `user_ip_address` (
 `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `uuid` varchar(40) DEFAULT NULL,
  `account_id` bigint(20) unsigned DEFAULT NULL,
  `domain_id` bigint(20) unsigned DEFAULT NULL,
  `public_ip_address` char(40) NOT NULL,
  `data_center_id` bigint(20) unsigned NOT NULL,
  `source_nat` int(1) unsigned NOT NULL DEFAULT '0',
  `allocated` datetime DEFAULT NULL,
  `vlan_db_id` bigint(20) unsigned NOT NULL,
  `one_to_one_nat` int(1) unsigned NOT NULL DEFAULT '0',
  `vm_id` bigint(20) unsigned DEFAULT NULL,
  `state` char(32) NOT NULL DEFAULT 'Free',
  `mac_address` bigint(20) unsigned NOT NULL,
  `source_network_id` bigint(20) unsigned NOT NULL,
  `network_id` bigint(20) unsigned DEFAULT NULL,
  `physical_network_id` bigint(20) unsigned NOT NULL,
  `ip_acl_id` bigint(20) unsigned NOT NULL,
  `is_system` int(1) unsigned NOT NULL DEFAULT '0',
  `vpc_id` bigint(20) unsigned DEFAULT NULL,
  `dnat_vmip` varchar(40) DEFAULT NULL,
  `is_portable` int(1) unsigned NOT NULL DEFAULT '0',
  `display` tinyint(1) NOT NULL DEFAULT '1',
  `removed` datetime DEFAULT NULL,
  `created` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uc_user_ip_address__uuid` (`uuid`),
  UNIQUE KEY `public_ip_address` (`public_ip_address`,`source_network_id`,`removed`),
  KEY `fk_user_ip_address__source_network_id` (`source_network_id`),
  KEY `fk_user_ip_address__network_id` (`network_id`),
  KEY `fk_user_ip_address__account_id` (`account_id`),
  KEY `fk_user_ip_address__vm_id` (`vm_id`),
  KEY `fk_user_ip_address__vlan_db_id` (`vlan_db_id`),
  KEY `fk_user_ip_address__data_center_id` (`data_center_id`),
  KEY `fk_user_ip_address__physical_network_id` (`physical_network_id`),
  KEY `fk_user_ip_address__vpc_id` (`vpc_id`),
  KEY `i_user_ip_address__allocated` (`allocated`),
  KEY `i_user_ip_address__source_nat` (`source_nat`)
--   CONSTRAINT `fk_user_ip_address__account_id` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`),
--   CONSTRAINT `fk_user_ip_address__data_center_id` FOREIGN KEY (`data_center_id`) REFERENCES `data_center` (`id`) ON DELETE CASCADE,
--   CONSTRAINT `fk_user_ip_address__network_id` FOREIGN KEY (`network_id`) REFERENCES `networks` (`id`),
--   CONSTRAINT `fk_user_ip_address__physical_network_id` FOREIGN KEY (`physical_network_id`) REFERENCES `physical_network` (`id`) ON DELETE CASCADE,
--   CONSTRAINT `fk_user_ip_address__source_network_id` FOREIGN KEY (`source_network_id`) REFERENCES `networks` (`id`),
--   CONSTRAINT `fk_user_ip_address__vlan_db_id` FOREIGN KEY (`vlan_db_id`) REFERENCES `vlan` (`id`) ON DELETE CASCADE,
--   CONSTRAINT `fk_user_ip_address__vm_id` FOREIGN KEY (`vm_id`) REFERENCES `vm_instance` (`id`),
--   CONSTRAINT `fk_user_ip_address__vpc_id` FOREIGN KEY (`vpc_id`) REFERENCES `vpc` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8;