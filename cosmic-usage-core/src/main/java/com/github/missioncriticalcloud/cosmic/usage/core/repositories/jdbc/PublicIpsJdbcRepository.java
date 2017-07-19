package com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc;

import java.util.Properties;

import com.github.missioncriticalcloud.cosmic.usage.core.model.PublicIp;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.PublicIpsRepository;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc.mappers.PublicIpMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PublicIpsJdbcRepository implements PublicIpsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Properties queries;
    private final PublicIpMapper publicIpMapper;

    @Autowired
    public PublicIpsJdbcRepository(
            final NamedParameterJdbcTemplate jdbcTemplate,
            @Qualifier("queries") final Properties queries,
            final PublicIpMapper publicIpMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.queries = queries;
        this.publicIpMapper = publicIpMapper;
    }

    @Override
    public PublicIp get(final String uuid) {
        try {
            return jdbcTemplate.queryForObject(
                    queries.getProperty("public-ips-repository.get-public-ip"),
                    new MapSqlParameterSource("uuid", uuid),
                    publicIpMapper
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
