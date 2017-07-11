INSERT INTO `domain`
  (id, uuid, owner, path, name)
VALUES
  (1, 'domain_uuid1', 1, '/', 'ROOT');

INSERT INTO `service_offering`
  (id, cpu, ram_size)
VALUES
  (1, 2, 4096);

INSERT INTO `volumes`
  (id, domain_id, size, data_center_id, volume_type, disk_offering_id, uuid, state, device_id)
VALUES
  (1, 1, 1048576, 1, 'ROOT', 1, 'storage_uuid1', 'Ready', 0);
