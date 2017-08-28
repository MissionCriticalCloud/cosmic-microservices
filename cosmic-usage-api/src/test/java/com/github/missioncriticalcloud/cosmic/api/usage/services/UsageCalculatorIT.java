package com.github.missioncriticalcloud.cosmic.api.usage.services;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DATE_FORMATTER;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.exceptions.NoMetricsFoundException;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Compute;
import com.github.missioncriticalcloud.cosmic.usage.core.model.DataUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Networking;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Report;
import com.github.missioncriticalcloud.cosmic.usage.core.model.TimeUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Usage;
import com.github.missioncriticalcloud.cosmic.usage.testresources.EsTestUtils;
import io.searchbox.client.JestClient;
import org.joda.time.DateTime;
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
@Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
public class UsageCalculatorIT {

    @Autowired
    private JestClient jestClient;

    @Autowired
    private UsageCalculator usageCalculator;

    @Test(expected = NoMetricsFoundException.class)
    public void testNoMetricsInterval1() throws IOException {
        EsTestUtils.setupIndex(jestClient);

        final DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2017-01-02");
        final String path = null;

        usageCalculator.calculate(from, to, path, DataUnit.BYTES, TimeUnit.SECONDS, false);
    }

    @Test(expected = NoMetricsFoundException.class)
    public void testNoMetricsInterval2() throws IOException {
        EsTestUtils.setupIndex(jestClient);
        EsTestUtils.setupData(jestClient);

        final DateTime from = DATE_FORMATTER.parseDateTime("2000-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2000-01-01");
        final String path = null;

        usageCalculator.calculate(from, to, path, DataUnit.BYTES, TimeUnit.SECONDS, false);
    }

    @Test
    public void testRootPath() throws IOException {
        EsTestUtils.setupIndex(jestClient);
        EsTestUtils.setupData(jestClient);

        final DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2017-01-02");
        final String path = "/";

        final Report report = usageCalculator.calculate(from, to, path, DataUnit.BYTES, TimeUnit.SECONDS, false);
        assertThat(report).isNotNull();

        final List<Domain> domains = report.getDomains();
        assertThat(domains).isNotNull();
        assertThat(domains).isNotEmpty();
        assertThat(domains).hasSize(2);

        assertDomain1(domains);
        assertDomain2(domains);
    }

    @Test
    public void testLevel1Path() throws IOException {
        EsTestUtils.setupIndex(jestClient);
        EsTestUtils.setupData(jestClient);

        final DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2017-01-02");
        final String path = "/level1";

        final Report report = usageCalculator.calculate(from, to, path, DataUnit.BYTES, TimeUnit.SECONDS, false);
        assertThat(report).isNotNull();

        final List<Domain> domains = report.getDomains();
        assertThat(domains).isNotNull();
        assertThat(domains).isNotEmpty();
        assertThat(domains).hasSize(1);

        assertDomain2(domains);
    }

    @Test(expected = NoMetricsFoundException.class)
    public void testLevel2Path() throws Exception {
        EsTestUtils.setupIndex(jestClient);
        EsTestUtils.setupData(jestClient);

        final DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2017-01-02");
        final String path = "/level1/level2";

        usageCalculator.calculate(from, to, path, DataUnit.BYTES, TimeUnit.SECONDS, false);
    }

    private void assertDomain1(final List<Domain> domains) {
        domains.stream().filter(domain -> "domain_uuid1".equals(domain.getUuid())).forEach(domain -> {
            assertThat(domain.getName()).isNotNull();
            assertThat(domain.getName()).isEqualTo("ROOT");
            assertThat(domain.getPath()).isEqualTo("/");

            final Usage usage = domain.getUsage();
            assertThat(usage).isNotNull();

            assertCompute(usage.getCompute(), 2, 400);
            assertThat(usage.getStorage().getTotal().get(0).getSize()).isEqualByComparingTo(BigDecimal.valueOf(144000));
            assertNetwork(usage.getNetworking(), 900);
        });
    }

    private void assertDomain2(final List<Domain> domains) {
        domains.stream().filter(domain -> "domain_uuid2".equals(domain.getUuid())).forEach(domain -> {
            assertThat(domain.getName()).isNotNull();
            assertThat(domain.getName()).isEqualTo("level1");
            assertThat(domain.getPath()).isEqualTo("/level1");

            final Usage usage = domain.getUsage();
            assertThat(usage).isNotNull();

            assertCompute(usage.getCompute(), 4, 800);
            assertThat(usage.getStorage().getTotal().get(0).getSize()).isEqualByComparingTo(BigDecimal.valueOf(288000));
            assertNetwork(usage.getNetworking(), 900);
        });
    }

    private void assertCompute(final Compute compute, double expectedCpu, double expectedMemory) {
        assertThat(compute).isNotNull();

        final BigDecimal cpu = compute.getInstanceTypes().get(0).getCpu();
        assertThat(cpu).isNotNull();
        assertThat(cpu).isEqualByComparingTo(BigDecimal.valueOf(expectedCpu));

        final BigDecimal memory = compute.getInstanceTypes().get(0).getMemory();
        assertThat(memory).isNotNull();
        assertThat(memory).isEqualByComparingTo(BigDecimal.valueOf(expectedMemory));
    }

    private void assertNetwork(final Networking networking, double expectedPublicIps) {
        assertThat(networking).isNotNull();

        final BigDecimal publicIps = networking.getTotal().getPublicIps();
        assertThat(publicIps).isNotNull();
        assertThat(publicIps).isEqualByComparingTo(BigDecimal.valueOf(expectedPublicIps));
    }
}
