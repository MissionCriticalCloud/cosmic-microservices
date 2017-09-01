package com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc;

import java.util.List;
import java.util.Properties;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Tag;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Volume;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.VolumesRepository;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc.mappers.TagMapper;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc.mappers.VolumeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VolumesJdbcRepository implements VolumesRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Properties queries;
    private final VolumeMapper volumeMapper;
    private final TagMapper tagMapper;

    @Autowired
    public VolumesJdbcRepository(
            final NamedParameterJdbcTemplate jdbcTemplate,
            @Qualifier("queries") final Properties queries,
            final VolumeMapper volumeMapper,
            final TagMapper tagMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.queries = queries;
        this.volumeMapper = volumeMapper;
        this.tagMapper = tagMapper;
    }

    @Override
    public Volume get(final String uuid) {
        try {
            final Volume volume = jdbcTemplate.queryForObject(
                    queries.getProperty("volumes-repository.get-volume"),
                    new MapSqlParameterSource("uuid", uuid),
                    volumeMapper
            );

            final List<Tag> tags = jdbcTemplate.query(
                    queries.getProperty("volumes-repository.get-volume-tags"),
                    new MapSqlParameterSource("uuid", uuid),
                    tagMapper
            );

            volume.getTags().addAll(tags);

            return volume;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
