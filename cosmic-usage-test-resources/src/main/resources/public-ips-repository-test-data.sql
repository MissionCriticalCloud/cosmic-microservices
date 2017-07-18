INSERT INTO `domain`
  (id, uuid, owner, path)
VALUES
  (1, 'domain_uuid1', 1, 'path1');

INSERT INTO `service_offering`
  (id, cpu, ram_size)
VALUES
  (1, 2, 4096);

INSERT INTO `user_ip_address`
  (id, uuid, domain_id, public_ip_address, data_center_id, allocated, vlan_db_id, state, mac_address, source_network_id, physical_network_id, ip_acl_id)
VALUES
  (1, 'ip_uuid1', 1, '85.1.1.1', 1, now(), 1, 'Allocated', 1, 1, 1, 1);

INSERT INTO `vpc`
  (id, uuid, vpc_offering_id, zone_id, state, domain_id, created)
VALUES
  (1, 'vpc_uuid1', 1, 1, 'Enabled', 1, now());

INSERT INTO `networks`
  (id, traffic_type, broadcast_domain_type, network_offering_id, data_center_id, guru_name, state, related, domain_id, created)
VALUES
  (1, 'Guest', 'Vlan',1, 1, 'PrivateNetworkGuru', 'Implemented', 206, 1, now());
