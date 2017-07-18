package com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.missioncriticalcloud.cosmic.usage.core.exceptions.NoMetricsFoundException;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.DomainsRepository;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc.mappers.DomainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DomainsJdbcRepository implements DomainsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Properties queries;
    private final DomainMapper domainMapper;

    @Autowired
    public DomainsJdbcRepository(
            final NamedParameterJdbcTemplate jdbcTemplate,
            @Qualifier("queries") final Properties queries,
            final DomainMapper domainMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.queries = queries;
        this.domainMapper = domainMapper;
    }

    @Override
    public List<Domain> search(final String path) {
        return jdbcTemplate.query(
                queries.getProperty("domains-repository.search-domains"),
                new MapSqlParameterSource("path", path + "%"),
                domainMapper
        );
    }

    @Override
    public Domain get(final String path) {
        return jdbcTemplate.queryForObject(
                queries.getProperty("domains-repository.get-domain"),
                new MapSqlParameterSource("path", path),
                domainMapper
        );
    }

    @Override
    public Domain getByUuid(final String uuid) {
        return jdbcTemplate.queryForObject(
                queries.getProperty("domains-repository.get-domain-by-uuid"),
                new MapSqlParameterSource("uuid", uuid),
                domainMapper
        );
    }

    @Override
    public Map<String, Domain> map(final String path, final boolean detailed) {
        final List<Domain> domains = detailed ? Collections.singletonList(get(path)) : search(path);
        if (domains.isEmpty()) {
            throw new NoMetricsFoundException();
        }

        final Map<String, Domain> domainsMap = new HashMap<>();
        domainsMap.putAll(domains.stream().collect(Collectors.toMap(Domain::getUuid, Function.identity())));

        return domainsMap;
    }
}
