package com.github.missioncriticalcloud.cosmic.metricscollector.repositories.jdbc;

import java.util.List;
import java.util.Properties;

import com.github.missioncriticalcloud.cosmic.metricscollector.repositories.MetricsRepository;
import com.github.missioncriticalcloud.cosmic.metricscollector.repositories.mappers.VirtualMachineMetricsMapper;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Metric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository("virtualMachineMetricsRepository")
public class VirtualMachineMetricsJdbcRepository implements MetricsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Properties queries;
    private final VirtualMachineMetricsMapper metricsMapper;

    @Autowired
    public VirtualMachineMetricsJdbcRepository(
            final NamedParameterJdbcTemplate jdbcTemplate,
            @Qualifier("queries") final Properties queries,
            final VirtualMachineMetricsMapper metricsMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.queries = queries;
        this.metricsMapper = metricsMapper;
    }

    @Override
    public List<Metric> getMetrics() {
        return jdbcTemplate.query(
                queries.getProperty("virtual-machine-metrics-repository.get-metrics"),
                metricsMapper
        );
    }
}
