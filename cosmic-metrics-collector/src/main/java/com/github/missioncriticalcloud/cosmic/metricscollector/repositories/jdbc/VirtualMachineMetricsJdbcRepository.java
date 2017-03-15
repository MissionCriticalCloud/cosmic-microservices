package com.github.missioncriticalcloud.cosmic.metricscollector.repositories.jdbc;

import java.util.List;
import java.util.Properties;

import com.github.missioncriticalcloud.cosmic.metricscollector.model.Metric;
import com.github.missioncriticalcloud.cosmic.metricscollector.repositories.VirtualMachineMetricsRepository;
import com.github.missioncriticalcloud.cosmic.metricscollector.repositories.mappers.VirtualMachineMetricsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VirtualMachineMetricsJdbcRepository implements VirtualMachineMetricsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Properties queries;

    @Autowired
    public VirtualMachineMetricsJdbcRepository(final NamedParameterJdbcTemplate jdbcTemplate, @Qualifier("queries") final Properties queries) {
        this.jdbcTemplate = jdbcTemplate;
        this.queries = queries;
    }

    @Override
    public List<Metric> getMetrics() {
        return jdbcTemplate.query(queries.getProperty("virtual-machine-metrics-repository.get-metrics"), new VirtualMachineMetricsMapper());
    }

}
