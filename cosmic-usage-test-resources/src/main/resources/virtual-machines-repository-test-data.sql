INSERT INTO `domain`
  (id, uuid, owner, path, name)
VALUES
  (1, 'domain_uuid1', 1, '/', 'ROOT');

INSERT INTO `vm_template`
  (id, unique_name, name, uuid, public, featured, hvm, bits, format, created, guest_os_id)
VALUES
  (1, 'template1', 'template1', 'template_uuid1', 1, 1, 1, 1, 'format1', now(),  1);

INSERT INTO `vm_instance`
  (id, name, uuid, instance_name, state, guest_os_id, data_center_id, vnc_password, created, type, vm_type, domain_id, service_offering_id, vm_template_id)
VALUES
  (1, 'vm1', 'vm_instance_uuid1', 'vm1', 'Running', 1, 1, 'vnc_password1', now(), 'User', 'vm_type1', 1, 1, 1);

INSERT INTO `resource_tags`
  (`id`, `uuid`, `key`, `value`, `resource_id`, `resource_uuid`, `resource_type`, `domain_id`, `account_id`)
VALUES
  (1, 'tag_uuid1', 'key1', 'value1', 1, 'vm_instance_uuid1', 'UserVm', 1, 1),
  (2, 'tag_uuid2', 'key2', 'value2', 1, 'vm_instance_uuid1', 'UserVm', 1, 1);
