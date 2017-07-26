package com.github.missioncriticalcloud.cosmic.billingreporter.services;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DATE_FORMATTER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
public class MailServiceIT {

    @SpyBean
    private MailService mailService;

    @SpyBean
    private JavaMailSender mailSender;

    @Before
    public void setupMock() {
        doNothing().when(mailSender).send(any(MimeMessage.class));
    }

    @Test
    public void testMailServiceGetMimeMessageWithEmailAddress() throws IOException {
        DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        DateTime to = DATE_FORMATTER.parseDateTime("2017-01-31");

        final Domain domainNormal = new Domain("99FCADB6-61C3-44A0-A330-B90ADB0301F4");
        domainNormal.setEmail(UUID.randomUUID() + "@cosmic.nl");
        domainNormal.setPath("domain/normal");
        domainNormal.setName("normal");

        final List<Domain> domains = new LinkedList<>();

        domains.add(domainNormal);

        doAnswer(invocationOnMock -> {
            MimeMessage message = invocationOnMock.getArgumentAt(0, MimeMessage.class);
            assertThat(message.getDataHandler().getContent().toString()).contains("<span>" + domainNormal.getName() + "</span>");
            assertThat(message.getAllRecipients()).hasSize(1);
            assertThat(message.getAllRecipients()[0].toString()).isEqualTo(domainNormal.getEmail());

            return true;
        }).when(mailSender).send(any(MimeMessage.class));

        mailService.sendEmail(domains, from, to);
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    public void testMailServiceGetMimeMessageWithoutEmailAddress() throws IOException {
        DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        DateTime to = DATE_FORMATTER.parseDateTime("2017-01-31");

        final Domain domainWithoutEmail = new Domain("3144A67D-A6F0-49C6-9E7C-FAAE905C4EC3");
        domainWithoutEmail.setPath("domain/withoutemail");
        domainWithoutEmail.setName("without email");

        final List<Domain> domains = new LinkedList<>();

        domains.add(domainWithoutEmail);
        mailService.sendEmail(domains, from, to);
        verify(mailSender, times(0)).send(any(MimeMessage.class));
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

        verify(mailService, times(1)).sendEmail(domains, from, to);
    }

}
