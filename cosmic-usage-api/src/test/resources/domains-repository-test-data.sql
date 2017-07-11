INSERT INTO `domain`
  (id, uuid, owner, path, name)
VALUES
  (1, 'domain_uuid1', 1, '/', 'ROOT'),
  (2, 'domain_uuid2', 2, '/level1', 'level1'),
  (3, 'domain_uuid3', 3, '/level1/level2', 'level2');

INSERT INTO `vm_template`
  (id, unique_name, name, uuid, public, featured, hvm, bits, format, created, guest_os_id)
VALUES
  (1, 'template1', 'template1', 'template_uuid1', 1, 1, 1, 1, 'format1', now(),  1);

INSERT INTO `vm_instance`
  (id, name, uuid, instance_name, state, guest_os_id, data_center_id, vnc_password, created, type, vm_type, domain_id, service_offering_id, vm_template_id)
VALUES
  (1, 'vm1', 'vm_instance_uuid1', 'vm1', 'Running', 1, 1, 'vnc_password1', now(), 'User', 'vm_type1', 1, 1, 1),
  (2, 'vm2', 'vm_instance_uuid2', 'vm2', 'Running', 1, 1, 'vnc_password1', now(), 'User', 'vm_type1', 1, 1, 1),
  (3, 'vm3', 'vm_instance_uuid3', 'vm3', 'Running', 1, 1, 'vnc_password1', now(), 'User', 'vm_type1', 1, 1, 1),
  (4, 'vm4', 'vm_instance_uuid4', 'vm4', 'Running', 1, 1, 'vnc_password1', now(), 'User', 'vm_type1', 1, 1, 1),
  (5, 'vm5', 'vm_instance_uuid5', 'vm5', 'Running', 1, 1, 'vnc_password1', now(), 'User', 'vm_type1', 1, 1, 1),
  (6, 'vm6', 'vm_instance_uuid6', 'vm6', 'Running', 1, 1, 'vnc_password1', now(), 'User', 'vm_type1', 1, 1, 1);
