INSERT INTO `domain` (id, uuid, owner, path, name, email)
VALUES (1, 'domain_uuid1', 1, '/', 'ROOT', 'root@cosmic.nl'), (2, 'domain_uuid2', 2, '/level1', 'level1', 'level1@cosmic.nl'),
  (3, 'domain_uuid3', 3, '/level1/level2', 'level2', 'level2@cosmic.nl');

INSERT INTO `vm_template` (id, unique_name, name, uuid, public, featured, hvm, bits, format, created, guest_os_id)
VALUES (1, 'template1', 'template1', 'template_uuid1', 1, 1, 1, 1, 'format1', now(), 1);

INSERT INTO `vm_instance` (id, name, uuid, instance_name, state, guest_os_id, data_center_id, vnc_password, created, type, vm_type, domain_id, service_offering_id, vm_template_id)
VALUES (1, 'vm1', 'vm_instance_uuid1', 'vm1', 'Running', 1, 1, 'vnc_password1', now(), 'User', 'vm_type1', 1, 1, 1),
  (2, 'vm2', 'vm_instance_uuid2', 'vm2', 'Running', 1, 1, 'vnc_password1', now(), 'User', 'vm_type1', 1, 1, 1),
  (3, 'vm3', 'vm_instance_uuid3', 'vm3', 'Running', 1, 1, 'vnc_password1', now(), 'User', 'vm_type1', 1, 1, 1),
  (4, 'vm4', 'vm_instance_uuid4', 'vm4', 'Running', 1, 1, 'vnc_password1', now(), 'User', 'vm_type1', 1, 1, 1),
  (5, 'vm5', 'vm_instance_uuid5', 'vm5', 'Running', 1, 1, 'vnc_password1', now(), 'User', 'vm_type1', 1, 1, 1),
  (6, 'vm6', 'vm_instance_uuid6', 'vm6', 'Running', 1, 1, 'vnc_password1', now(), 'User', 'vm_type1', 1, 1, 1);

INSERT INTO `volumes` (id, domain_id, size, data_center_id, volume_type, disk_offering_id, uuid, state, device_id)
VALUES (1, 1, 1048576, 1, 'ROOT', 1, 'storage_uuid1', 'Ready', 0), (2, 1, 1048576, 1, 'level1', 1, 'storage_uuid2', 'Ready', 0);

INSERT INTO `user_ip_address` (id, uuid, domain_id, public_ip_address, data_center_id, allocated, vlan_db_id, state, mac_address, source_network_id, physical_network_id, ip_acl_id)
VALUES (1, 'ip_uuid1', 1, '85.1.1.1', 1, now(), 1, 'Allocated', 1, 1, 1, 1);