package com.github.missioncriticalcloud.cosmic.api.usage.services;

import static com.github.missioncriticalcloud.cosmic.api.usage.utils.FormatUtils.DATE_FORMATTER;
import static java.math.BigDecimal.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.api.usage.model.Domain;
import com.github.missioncriticalcloud.cosmic.api.usage.model.Resource;
import com.github.missioncriticalcloud.cosmic.api.usage.model.State;
import com.github.missioncriticalcloud.cosmic.api.usage.model.Usage;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class UsageServiceTest {

    @SpyBean
    private UsageService usageService;

    @Test
    public void calculateDomainsUsageTest1() {
        final List<Domain> domains = setupSingleDomain(
                5760,
                3,
                3072
        );

        final DateTime from = DATE_FORMATTER.parseDateTime("2016-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2016-01-02");

        assertSingleDomainUsage(
                usageService.calculateDomainsUsage(domains, from, to),
                2,
                2048
        );
    }

    @Test
    public void calculateDomainsUsageTest2() {
        final List<Domain> domains = setupSingleDomain(
                4320,
                3,
                3072
        );

        final DateTime from = DATE_FORMATTER.parseDateTime("2016-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2016-01-02");

        assertSingleDomainUsage(
                usageService.calculateDomainsUsage(domains, from, to),
                1.5,
                1536
        );
    }

    @Test
    public void calculateDomainsUsageTest3() {
        final List<Domain> domains = setupSingleDomain(
                2160,
                4,
                4096
        );

        final DateTime from = DATE_FORMATTER.parseDateTime("2016-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2016-01-02");

        assertSingleDomainUsage(
                usageService.calculateDomainsUsage(domains, from, to),
                1,
                1024
        );
    }

    @Test
    public void calculateDomainsUsageTest4() {
        final List<Domain> domains = setupMultipleDomains(
                5760,
                3,
                3072,
                4320,
                3,
                3072
        );

        final DateTime from = DATE_FORMATTER.parseDateTime("2016-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2016-01-02");

        assertMultipleDomainsUsage(
                usageService.calculateDomainsUsage(domains, from, to),
                2,
                2048,
                1.5,
                1536
        );
    }

    @Test
    public void calculateDomainsUsageTest5() {
        final List<Domain> domains = setupMultipleDomains(
                4320,
                3,
                3072,
                2160,
                4,
                4096
        );

        final DateTime from = DATE_FORMATTER.parseDateTime("2016-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2016-01-02");

        assertMultipleDomainsUsage(
                usageService.calculateDomainsUsage(domains, from, to),
                1.5,
                1536,
                1,
                1024
        );
    }

    @Test
    public void calculateDomainsUsageTest6() {
        final List<Domain> domains = setupMultipleDomains(
                2160,
                4,
                4096,
                4320,
                3,
                3072
        );

        final DateTime from = DATE_FORMATTER.parseDateTime("2016-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2016-01-02");

        assertMultipleDomainsUsage(
                usageService.calculateDomainsUsage(domains, from, to),
                1,
                1024,
                1.5,
                1536
        );
    }

    private List<Domain> setupSingleDomain(
            final long runningSampleCount,
            final double runningCpu,
            final double runningMemory
    ) {
        final List<Domain> domains = new ArrayList<>(1);

        final Domain domain = new Domain();
        domain.setUuid("1");
        domains.add(domain);

        final Resource resource = new Resource();
        resource.setUuid("1");
        domain.getResources().add(resource);

        final State running = new State(
                State.RUNNING,
                valueOf(runningSampleCount),
                valueOf(runningCpu),
                valueOf(runningMemory)
        );
        resource.getStates().add(running);

        return domains;
    }

    private List<Domain> setupMultipleDomains(
            final long runningSampleCount1,
            final double runningCpu1,
            final double runningMemory1,
            final long runningSampleCount2,
            final double runningCpu2,
            final double runningMemory2
    ) {
        final List<Domain> domains = new ArrayList<>(2);

        final Domain domain1 = new Domain();
        domain1.setUuid("1");
        domains.add(domain1);

        final Resource resource1 = new Resource();
        resource1.setUuid("1");
        domain1.getResources().add(resource1);

        final State running1 = new State(
                State.RUNNING,
                valueOf(runningSampleCount1),
                valueOf(runningCpu1),
                valueOf(runningMemory1)
        );
        resource1.getStates().add(running1);

        final Domain domain2 = new Domain();
        domain2.setUuid("2");
        domains.add(domain2);

        final Resource resource2 = new Resource();
        resource2.setUuid("2");
        domain2.getResources().add(resource2);

        final State running2 = new State(
                State.RUNNING,
                valueOf(runningSampleCount2),
                valueOf(runningCpu2),
                valueOf(runningMemory2)
        );
        resource2.getStates().add(running2);

        return domains;
    }

    private void assertSingleDomainUsage(
            final List<Usage> domainsUsage,
            final double expectedCpu,
            final double expectedMemory
    ) {
        assertThat(domainsUsage).isNotEmpty();
        assertThat(domainsUsage).hasSize(1);

        final Usage usage = domainsUsage.get(0);
        assertThat(usage).isNotNull();
        assertThat(usage.getDomain()).isNotNull();
        assertThat(usage.getCpu()).isEqualByComparingTo(valueOf(expectedCpu));
        assertThat(usage.getMemory()).isEqualByComparingTo(valueOf(expectedMemory));
    }

    private void assertMultipleDomainsUsage(
            final List<Usage> domainsUsage,
            final double expectedCpu1,
            final double expectedMemory1,
            final double expectedCpu2,
            final double expectedMemory2
    ) {
        assertThat(domainsUsage).isNotEmpty();
        assertThat(domainsUsage).hasSize(2);

        final Usage usage1 = domainsUsage.get(0);
        assertThat(usage1).isNotNull();
        assertThat(usage1.getDomain()).isNotNull();
        assertThat(usage1.getCpu()).isEqualByComparingTo(valueOf(expectedCpu1));
        assertThat(usage1.getMemory()).isEqualByComparingTo(valueOf(expectedMemory1));

        final Usage usage2 = domainsUsage.get(1);
        assertThat(usage2).isNotNull();
        assertThat(usage2.getDomain()).isNotNull();
        assertThat(usage2.getCpu()).isEqualByComparingTo(valueOf(expectedCpu2));
        assertThat(usage2.getMemory()).isEqualByComparingTo(valueOf(expectedMemory2));
    }

}
