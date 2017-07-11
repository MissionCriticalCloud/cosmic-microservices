package com.github.missioncriticalcloud.cosmic.api.usage.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.missioncriticalcloud.cosmic.api.usage.repositories.jdbc.VirtualMachinesJdbcRepository;
import com.github.missioncriticalcloud.cosmic.usage.core.model.VirtualMachine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class VirtualMachinesRepositoryIT {

    @Autowired
    private VirtualMachinesRepository virtualMachinesRepository;

    @Test
    @Sql(value = {"/test-schema.sql", "/virtual-machines-repository-test-data.sql"})
    public void testVirtualMachineRepository() {
        VirtualMachine virtualMachine = virtualMachinesRepository.get("uuid_not_exists");
        assertThat(virtualMachine).isNull();

        virtualMachine = virtualMachinesRepository.get("vm_instance_uuid1");
        assertThat(virtualMachine).isNotNull();
        assertThat(virtualMachine.getCpu()).isNotNull();
    }

}
