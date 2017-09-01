package com.github.missioncriticalcloud.cosmic.usage.core.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Tag;
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

        assertThat(volume.getVolumeSizes()).isNotNull();
        assertThat(volume.getVolumeSizes()).isEmpty();

        final List<Tag> tags = volume.getTags();
        assertThat(tags).isNotNull();
        assertThat(tags).isEmpty();
        assertThat(tags).hasSize(2);

        final Tag tag1 = tags.get(0);
        assertThat(tag1).isNotNull();
        assertThat(tag1.getUuid()).isEqualTo("tag_uuid1");
        assertThat(tag1.getKey()).isEqualTo("key1");
        assertThat(tag1.getValue()).isEqualTo("value1");

        final Tag tag2 = tags.get(1);
        assertThat(tag2).isNotNull();
        assertThat(tag2.getUuid()).isEqualTo("tag_uuid2");
        assertThat(tag2.getKey()).isEqualTo("key2");
        assertThat(tag2.getValue()).isEqualTo("value2");
    }
}
