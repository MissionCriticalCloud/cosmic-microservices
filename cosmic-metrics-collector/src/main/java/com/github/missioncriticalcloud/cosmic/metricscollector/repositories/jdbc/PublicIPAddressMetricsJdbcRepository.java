package com.github.missioncriticalcloud.cosmic.metricscollector.repositories.jdbc;

import com.github.missioncriticalcloud.cosmic.metricscollector.model.Metric;
import com.github.missioncriticalcloud.cosmic.metricscollector.repositories.PublicIPAddressMetricsRepository;
import com.github.missioncriticalcloud.cosmic.metricscollector.repositories.mappers.PublicIPAddressMetricsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Properties;

/**
 * Created by ikrstic on 14/03/2017.
 */
@Repository
public class PublicIPAddressMetricsJdbcRepository implements PublicIPAddressMetricsRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Properties queries;

    @Autowired
    public PublicIPAddressMetricsJdbcRepository(final NamedParameterJdbcTemplate jdbcTemplate, @Qualifier("queries") final Properties queries) {
        this.jdbcTemplate = jdbcTemplate;
        this.queries = queries;
    }

    @Override
    public List<Metric> getMetrics() {
        return jdbcTemplate.query(queries.getProperty("public-ip-address-metrics-repository.get-metrics"), new PublicIPAddressMetricsMapper());
    }
}
