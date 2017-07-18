package com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc;

import java.util.Properties;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Volume;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.VolumesRepository;
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

    @Autowired
    public VolumesJdbcRepository(
            final NamedParameterJdbcTemplate jdbcTemplate,
            @Qualifier("queries") final Properties queries,
            final VolumeMapper volumeMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.queries = queries;
        this.volumeMapper = volumeMapper;
    }

    @Override
    public Volume get(final String uuid) {
        try {
            return jdbcTemplate.queryForObject(
                    queries.getProperty("volumes-repository.get-volume"),
                    new MapSqlParameterSource("uuid", uuid),
                    volumeMapper
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
