package com.github.missioncriticalcloud.cosmic.metricscollector.repositories.jdbc;

import java.util.List;
import java.util.Properties;

import com.github.missioncriticalcloud.cosmic.metricscollector.repositories.MetricsRepository;
import com.github.missioncriticalcloud.cosmic.metricscollector.repositories.mappers.VolumeMetricsMapper;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("volumeMetricsRepository")
public class VolumeMetricsJdbcRepository implements MetricsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Properties queries;
    private final VolumeMetricsMapper metricsMapper;

    @Autowired
    public VolumeMetricsJdbcRepository(
            final NamedParameterJdbcTemplate jdbcTemplate,
            @Qualifier("queries") final Properties queries,
            final VolumeMetricsMapper metricsMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.queries = queries;
        this.metricsMapper = metricsMapper;
    }

    @Override
    public List<Metric> getMetrics() {
        return jdbcTemplate.query(
                queries.getProperty("volume-metrics-repository.get-metrics"),
                metricsMapper
        );
    }
}
