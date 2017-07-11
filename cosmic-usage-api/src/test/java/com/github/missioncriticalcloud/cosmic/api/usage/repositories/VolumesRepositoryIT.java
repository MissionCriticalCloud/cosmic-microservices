package com.github.missioncriticalcloud.cosmic.api.usage.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.missioncriticalcloud.cosmic.api.usage.repositories.jdbc.VolumesJdbcRepository;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Volume;
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
public class VolumesRepositoryIT {

    @Autowired
    private VolumesRepository volumesRepository;

    @Test
    @Sql(value = {"/test-schema.sql", "/volumes-repository-test-data.sql"})
    public void testVolumesRepository() {
        Volume volume = volumesRepository.get("uuid_not_exists");
        assertThat(volume).isNull();

        volume = volumesRepository.get("storage_uuid1");
        assertThat(volume).isNotNull();
        assertThat(volume.getSize()).isNotNull();
    }

}
