package com.github.missioncriticalcloud.cosmic.metricscollector.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.github.missioncriticalcloud.cosmic.metricscollector.model.Metric;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class VirtualMachineMetricsRepositoryIT {

    @Autowired
    private VirtualMachineMetricsRepository vmMetricsRepository;

    @Test
    @Sql(value = "/test-schema.sql")
    public void testEmptyDatabase() {
        final List<Metric> metrics = vmMetricsRepository.getMetrics();
        assertThat(metrics).isNotNull();
        assertThat(metrics).isEmpty();
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/test-data.sql"})
    public void testNonEmptyDatabase() {
        final List<Metric> metrics = vmMetricsRepository.getMetrics();
        assertThat(metrics).isNotNull();
        assertThat(metrics).isNotEmpty();
        assertThat(metrics).hasSize(1);

        metrics.forEach(metric -> {
            assertThat(metric).isNotNull();
            assertThat(metric.getDomainUuid()).isNotNull();
            assertThat(metric.getAccountUuid()).isNotNull();
            assertThat(metric.getResourceUuid()).isNotNull();
            assertThat(metric.getResourceType()).isNotNull();
            assertThat(metric.getTimestamp()).isNotNull();
            assertThat(metric.getPayload()).isNotNull();
            assertThat(metric.getPayload()).isNotEmpty();
        });
    }

}
