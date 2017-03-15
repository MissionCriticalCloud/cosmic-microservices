package com.github.missioncriticalcloud.cosmic.api.usage.repositories.jdbc;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Properties;

import com.github.missioncriticalcloud.cosmic.api.usage.model.Domain;
import com.github.missioncriticalcloud.cosmic.api.usage.repositories.DomainsRepository;
import com.github.missioncriticalcloud.cosmic.api.usage.repositories.mappers.DomainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.validation.annotation.Validated;

@Repository
@Validated
public class DomainsJdbcRepository implements DomainsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Properties queries;

    @Autowired
    public DomainsJdbcRepository(final NamedParameterJdbcTemplate jdbcTemplate, @Qualifier("queries") final Properties queries) {
        this.jdbcTemplate = jdbcTemplate;
        this.queries = queries;
    }

    @Override
    public List<Domain> list(@NotNull final String path) {
        return jdbcTemplate.query(queries.getProperty("domains-repository.list-domains"), new MapSqlParameterSource("path", path + "%"), new DomainMapper());
    }

}
