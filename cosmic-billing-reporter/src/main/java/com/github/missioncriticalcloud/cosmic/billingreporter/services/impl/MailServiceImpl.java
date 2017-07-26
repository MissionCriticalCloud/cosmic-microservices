package com.github.missioncriticalcloud.cosmic.billingreporter.services.impl;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.HUMAN_READABLE_DATE_FORMATTER;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.billingreporter.exceptions.UnableToCreateEmailException;
import com.github.missioncriticalcloud.cosmic.billingreporter.services.MailService;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.DomainToken;
import com.github.missioncriticalcloud.cosmic.usage.core.services.TokenService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.util.StringUtils;

@Component
public class MailServiceImpl implements MailService {

    private JavaMailSender javaMailSender;
    private TemplateEngine templateEngine;
    private TokenService tokenService;
    private final String urlRoot;
    private final String fromEmailAddress;
    private final long tokenTtl;

    private static final String MESSAGE_SUBJECT = "Bill Report of %s";
    private static final String EMAIL_TEMPLATE_ENCODING = "UTF-8";
    private static final String EMAIL_TEMPLATE_NAME = "email-template";

    @Autowired
    public MailServiceImpl(final JavaMailSender javaMailSender,
                           final TemplateEngine templateEngine,
                           final TokenService tokenService,
                           @Value("${cosmic.billing-reporter.bill-viewer-base-url}") final String urlRoot,
                           @Value("${cosmic.billing-reporter.from-mail-address}") final String fromEmailAddress,
                           @Value("${cosmic.billing-reporter.token-ttl}") final long tokenTtl
    ) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.tokenService = tokenService;
        this.urlRoot = urlRoot;
        this.fromEmailAddress = fromEmailAddress;
        this.tokenTtl = tokenTtl;
    }

    @Override
    public void sendEmail(final List<Domain> domains, final DateTime from, final DateTime to) {
        domains.stream()
               .filter(domain -> !StringUtils.isEmpty(domain.getEmail()))
               .forEach(domain -> {
                   final MimeMessage mimeMessage = getMimeMessage(from, domain);
                   this.javaMailSender.send(mimeMessage);
               });
    }

    private MimeMessage getMimeMessage(final DateTime from, final Domain domain) {
        final DomainToken domainToken = new DomainToken(
                new DateTime().plus(tokenTtl),
                new DateTime(),
                domain.getPath()
        );

        String humanDate = HUMAN_READABLE_DATE_FORMATTER.print(from);
        final String domainUrl = String.format(urlRoot, domain.getPath(), tokenService.encrypt(domainToken));

        final Context context = new Context();
        context.setVariable("domain", domain.getName());
        context.setVariable("url", domainUrl);
        context.setVariable("month", humanDate);

        final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, EMAIL_TEMPLATE_ENCODING);
        final String htmlContent;

        try {
            htmlContent = this.templateEngine.process(EMAIL_TEMPLATE_NAME, context);

            messageHelper.setSubject(String.format(MESSAGE_SUBJECT, humanDate));
            messageHelper.setFrom(fromEmailAddress);
            messageHelper.setTo(domain.getEmail());
            messageHelper.setText(htmlContent, true);
        } catch (MessagingException e) {
            throw new UnableToCreateEmailException(e.getMessage(), e);
        }

        return mimeMessage;
    }

}
