package com.github.missioncriticalcloud.cosmic.billingreporter.services;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DATE_FORMATTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;

import java.io.IOException;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.testresources.EsTestUtils;
import io.searchbox.client.JestClient;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

    @MockBean
    private MailService mailService;

    @Before
    public void setupMock() {
        doNothing().when(mailService).sendEmail(anyListOf(Domain.class), any(DateTime.class), any(DateTime.class));
    }

    @Test
    @Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
    public void testEmptyDatabaseSearchDomains() throws IOException {
        EsTestUtils.setupIndex(jestClient);
        EsTestUtils.setupData(jestClient);

        DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        DateTime to = DATE_FORMATTER.parseDateTime("2017-01-31");


        doAnswer(invocationOnMock -> {
            List<Domain> domains = invocationOnMock.getArgumentAt(0, List.class);

            assertThat(domains.size()).isEqualTo(2);

            assertThat(domains).containsExactlyInAnyOrder(
                    new Domain("domain_uuid1"),
                    new Domain("domain_uuid2")
            );
            return true;
        }).when(mailService).sendEmail(anyListOf(Domain.class), eq(from), eq(to));

        billingReporter.createReport(from, to);
    }

}
