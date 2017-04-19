package com.github.missioncriticalcloud.cosmic.api.usage.repositories.jdbc;

import java.util.Properties;

import com.github.missioncriticalcloud.cosmic.api.usage.repositories.VolumeRepository;
import com.github.missioncriticalcloud.cosmic.api.usage.repositories.jdbc.mappers.VolumeMapper;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Volume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VolumeJdbcRepository implements VolumeRepository {


    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Properties queries;
    private final VolumeMapper volumeMapper;

    @Autowired
    public VolumeJdbcRepository(
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
        return jdbcTemplate.queryForObject(
                queries.getProperty("volume-repository.get-volume"),
                new MapSqlParameterSource("uuid", uuid),
                volumeMapper
        );
    }

}
