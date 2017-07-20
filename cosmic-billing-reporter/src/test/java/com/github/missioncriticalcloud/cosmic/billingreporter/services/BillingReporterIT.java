package com.github.missioncriticalcloud.cosmic.billingreporter.services;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DATE_FORMATTER;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
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
public class BillingReporterIT {

    @Autowired
    private BillingReporter billingReporter;

    @Autowired
    private JestClient jestClient;

    @Test
    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
    public void testEmptyDatabaseSearchDomains() throws IOException {
        EsTestUtils.setupIndex(jestClient);
        EsTestUtils.setupData(jestClient);

        DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        DateTime to = DATE_FORMATTER.parseDateTime("2017-01-31");

        List<Domain> domains = billingReporter.getBillableDomains(from, to);

        assertThat(domains.size()).isEqualTo(2);

        assertThat(domains).containsExactlyInAnyOrder(
                new Domain("domain_uuid1"),
                new Domain("domain_uuid2")
        );
    }

//    @Test
//    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
//    public void testCreateReport() throws IOException {
//        EsTestUtils.setupIndex(jestClient);
//        EsTestUtils.setupData(jestClient);
//
//        DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
//        DateTime to = DATE_FORMATTER.parseDateTime("2017-01-31");
//
//        billingReporter.createReport(from, to);
//    }
}
