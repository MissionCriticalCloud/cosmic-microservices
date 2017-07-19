package com.github.missioncriticalcloud.cosmic.metricscollector.repositories.jdbc;

import java.util.List;
import java.util.Properties;

import com.github.missioncriticalcloud.cosmic.metricscollector.repositories.MetricsRepository;
import com.github.missioncriticalcloud.cosmic.metricscollector.repositories.mappers.PublicIpMetricsMapper;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("publicIpMetricsRepository")
public class PublicIpMetricsJdbcRepository implements MetricsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Properties queries;
    private final PublicIpMetricsMapper metricsMapper;

    @Autowired
    public PublicIpMetricsJdbcRepository(
            final NamedParameterJdbcTemplate jdbcTemplate,
            @Qualifier("queries") final Properties queries,
            final PublicIpMetricsMapper metricsMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.queries = queries;
        this.metricsMapper = metricsMapper;
    }

    @Override
    public List<Metric> getMetrics() {
        return jdbcTemplate.query(
                queries.getProperty("public-ip-metrics-repository.get-metrics"),
                metricsMapper
        );
    }
}
