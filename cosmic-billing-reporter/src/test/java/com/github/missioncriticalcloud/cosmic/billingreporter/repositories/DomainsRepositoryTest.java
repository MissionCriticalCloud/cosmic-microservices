package com.github.missioncriticalcloud.cosmic.billingreporter.repositories;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DATE_FORMATTER;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.searchbox.client.JestClient;
import io.searchbox.core.Bulk;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.indices.Refresh;
import io.searchbox.indices.template.PutTemplate;
import org.joda.time.DateTime;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileCopyUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class DomainsRepositoryTest {

    @Autowired
    private DomainsRepository domainsRepository;

    @Autowired
    private JestClient client;

    private static ObjectMapper objectMapper;

    @Test
    public void testAggregationWihtoutData() throws IOException {
        setupIndex();

        DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        DateTime to = DATE_FORMATTER.parseDateTime("2017-01-31");

        List<String> domains = domainsRepository.listDomains(from, to);

        assertThat(domains).isNull();
    }

    @Test
    public void testAggregation() throws IOException {
        setupIndex();
        setupData();

        DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        DateTime to = DATE_FORMATTER.parseDateTime("2017-01-31");

        List<String> domains = domainsRepository.listDomains(from, to);

        assertThat(domains.contains("domain_uuid1")).isTrue();
        assertThat(domains.contains("domain_uuid2")).isTrue();
        assertThat(domains.contains("domain_uuid3")).isFalse();
    }

    @BeforeClass
    public static void setup() {
        objectMapper = new ObjectMapper();
    }

    private void setupIndex() throws IOException {
        final Resource resource = new ClassPathResource("/cosmic-metrics-template.json");
        final String template = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));

        client.execute(new Delete.Builder("cosmic-metrics-*").build());
        client.execute(new PutTemplate.Builder("cosmic-metrics-template", template).build());
    }

    private void setupData() throws IOException {
        final Resource resource = new ClassPathResource("/cosmic-metrics-es-data.json");
        final JsonNode jsonNode = objectMapper.readTree(resource.getInputStream());

        Bulk.Builder builder = new Bulk.Builder().defaultIndex("cosmic-metrics-2017.01.01").defaultType("metric");

        jsonNode.elements().forEachRemaining(metric -> builder.addAction(
                new Index.Builder(metric.toString()).build()
        ));

        client.execute(builder.build());

        client.execute(new Refresh.Builder().build());
    }
}
