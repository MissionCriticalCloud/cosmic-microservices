INSERT INTO `vm_instance`
  (id, name, uuid, instance_name, state, guest_os_id, data_center_id, vnc_password, created, type, vm_type, account_id, domain_id, service_offering_id)
VALUES
  (1, 'vm1', 'vm_instance_uuid1', 'vm1', 'Running', 1, 1, 'vnc_password1', now(), 'User', 'vm_type1', 1, 1, 1);

INSERT INTO `domain`
  (id, uuid, owner, path)
VALUES
  (1, 'domain_uuid1', 1, 'path1');

INSERT INTO `account`
  (id, uuid, type)
VALUES
  (1, 'account_uuid1', 1);

INSERT INTO `service_offering`
  (id, cpu, ram_size)
VALUES
  (1, 2, 4096);

INSERT INTO `user_ip_address`
  (id, uuid, account_id, domain_id, public_ip_address, data_center_id, allocated, vlan_db_id, state, mac_address, source_network_id, physical_network_id, ip_acl_id)
VALUES
  (1,	'ip_uuid1', 1, 1, '192.168.23.2',	1, now(), 1, 'Allocated', 22, 200, 200, 2);
