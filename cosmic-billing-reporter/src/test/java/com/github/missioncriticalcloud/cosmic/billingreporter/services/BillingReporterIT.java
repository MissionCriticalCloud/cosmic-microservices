package com.github.missioncriticalcloud.cosmic.billingreporter.services;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DATE_FORMATTER;
import static org.assertj.core.api.Assertions.assertThat;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.testresources.EsTestUtils;
import io.searchbox.client.JestClient;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.util.StringUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
@SpyBean(MailService.class)
@MockBean(JavaMailSender.class)
public class BillingReporterIT {

    @Autowired
    private BillingReporter billingReporter;

    @Autowired
    private JestClient jestClient;

    @Autowired
    private MailService mailService;

    @Autowired
    private JavaMailSender mailSender;

    @Bean
    @Primary
    private MailService mailService() {
        return Mockito.spy(MailService.class);
    }

    @Bean
    @Primary
    private JavaMailSender mailSender() {
        return Mockito.mock(JavaMailSender.class);
    }

    @Before
    public void setupMock() {
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        Mockito.doNothing().when(mailSender).send(message);

        Mockito.when(mailSender.createMimeMessage())
               .thenReturn(message);
    }

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

    @Test
    public void testMailServiceGetMimeMessageWithEmailAddress() throws IOException {
        DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        DateTime to = DATE_FORMATTER.parseDateTime("2017-01-31");

        final Domain domainNormal = new Domain("99FCADB6-61C3-44A0-A330-B90ADB0301F4");
        domainNormal.setEmail(UUID.randomUUID() + "@cosmic.nl");
        domainNormal.setPath("domain/normal");
        domainNormal.setName("normal");

        final Domain domainWithoutEmail = new Domain("3144A67D-A6F0-49C6-9E7C-FAAE905C4EC3");
        domainWithoutEmail.setPath("domain/withoutemail");
        domainWithoutEmail.setName("without email");

        final List<Domain> domains = new LinkedList<>();

        domains.add(domainNormal);
        domains.add(domainWithoutEmail);

        domains.stream()
               .filter(domain -> !StringUtils.isEmpty(domain.getEmail()))
               .forEach(domain -> {
                   MimeMessage message = mailService.getMimeMessage(from, to, domain);
                   try {
                       assertThat(message.getDataHandler().getContent().toString()).contains("<span>" + domain.getName() + "</span>");
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMailServiceGetMimeMessageWithoutEmailAddress() throws IOException {
        DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        DateTime to = DATE_FORMATTER.parseDateTime("2017-01-31");

        final Domain domainNormal = new Domain("99FCADB6-61C3-44A0-A330-B90ADB0301F4");
        domainNormal.setEmail(UUID.randomUUID() + "@cosmic.nl");
        domainNormal.setPath("domain/normal");
        domainNormal.setName("normal");

        final Domain domainWithoutEmail = new Domain("3144A67D-A6F0-49C6-9E7C-FAAE905C4EC3");
        domainWithoutEmail.setPath("domain/withoutemail");
        domainWithoutEmail.setName("without email");

        final List<Domain> domains = new LinkedList<>();

        domains.add(domainNormal);
        domains.add(domainWithoutEmail);

        domains.stream()
               .filter(domain -> StringUtils.isEmpty(domain.getEmail()))
               .forEach(domain -> mailService.getMimeMessage(from, to, domain));
    }

    @Test
    public void testMailServiceSendEmail() throws IOException {
        DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        DateTime to = DATE_FORMATTER.parseDateTime("2017-01-31");

        final Domain domainNormal = new Domain("99FCADB6-61C3-44A0-A330-B90ADB0301F4");
        domainNormal.setEmail(UUID.randomUUID() + "@cosmic.nl");
        domainNormal.setPath("domain/normal");
        domainNormal.setName("normal");

        final Domain domainWithoutEmail = new Domain("3144A67D-A6F0-49C6-9E7C-FAAE905C4EC3");
        domainWithoutEmail.setPath("domain/withoutemail");
        domainWithoutEmail.setName("without email");

        final List<Domain> domains = new LinkedList<>();

        domains.add(domainNormal);
        domains.add(domainWithoutEmail);

        mailService.sendEmail(domains, from, to);
        // do nothing
        Mockito.verify(mailService, Mockito.times(1)).sendEmail(domains, from, to);
        Mockito.verify(mailService, Mockito.times(1)).getMimeMessage(from, to, domainNormal);
        Mockito.verify(mailService, Mockito.times(0)).getMimeMessage(from, to, domainWithoutEmail);
    }
}
