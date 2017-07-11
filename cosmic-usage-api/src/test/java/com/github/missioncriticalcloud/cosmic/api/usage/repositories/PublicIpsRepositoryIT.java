package com.github.missioncriticalcloud.cosmic.api.usage.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.missioncriticalcloud.cosmic.api.usage.repositories.jdbc.PublicIpsJdbcRepository;
import com.github.missioncriticalcloud.cosmic.usage.core.model.PublicIp;
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
public class PublicIpsRepositoryIT {

    @Autowired
    private PublicIpsRepository publicIpsRepository;

    @Test
    @Sql(value = {"/test-schema.sql", "/public-ips-repository-test-data.sql"})
    public void testPublicIpsRepository() {
        PublicIp publicIp = publicIpsRepository.get("uuid_not_exists");
        assertThat(publicIp).isNull();

        publicIp = publicIpsRepository.get("ip_uuid1");
        assertThat(publicIp).isNotNull();
        assertThat(publicIp.getState()).isNotNull();
    }

}
