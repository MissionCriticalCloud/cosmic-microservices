package com.github.missioncriticalcloud.cosmic.billingreporter.repositories;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DATE_FORMATTER;

import java.util.List;

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
public class DomainsRepositoryTest {

    @Autowired
    private DomainsRepository domainsRepository;

    @Test
    public void testAggregation() {

        DateTime from = DATE_FORMATTER.parseDateTime("2017-07-01");
        DateTime to = DATE_FORMATTER.parseDateTime("2017-07-31");

        List<String> domains = domainsRepository.listDomains(from, to);
    }
}
