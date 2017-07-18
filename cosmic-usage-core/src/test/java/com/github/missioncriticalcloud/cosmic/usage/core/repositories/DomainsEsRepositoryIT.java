package com.github.missioncriticalcloud.cosmic.usage.core.repositories;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DATE_FORMATTER;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.usagetestresources.EsTestUtils;
import io.searchbox.client.JestClient;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class DomainsEsRepositoryIT {

    @Autowired
    private DomainsEsRepository domainsEsRepository;

    @Autowired
    private JestClient jestClient;

    @Test
    public void testAggregationWihtoutData() throws IOException {
        EsTestUtils.setupIndex(jestClient);

        DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        DateTime to = DATE_FORMATTER.parseDateTime("2017-01-31");

        List<String> domains = domainsEsRepository.listDomains(from, to);

        assertThat(domains).isNull();
    }

    @Test
    public void testAggregation() throws IOException {
        EsTestUtils.setupIndex(jestClient);
        EsTestUtils.setupData(jestClient);

        DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        DateTime to = DATE_FORMATTER.parseDateTime("2017-01-31");

        List<String> domains = domainsEsRepository.listDomains(from, to);

        assertThat(domains.contains("domain_uuid1")).isTrue();
        assertThat(domains.contains("domain_uuid2")).isTrue();
        assertThat(domains.contains("domain_uuid3")).isFalse();
    }
}
