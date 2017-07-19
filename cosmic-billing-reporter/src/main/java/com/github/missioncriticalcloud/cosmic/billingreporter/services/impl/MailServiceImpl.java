package com.github.missioncriticalcloud.cosmic.billingreporter.services.impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.billingreporter.exceptions.UnableToSendEmailException;
import com.github.missioncriticalcloud.cosmic.billingreporter.services.MailService;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class MailServiceImpl implements MailService {

    private JavaMailSender javaMailSender;
    private TemplateEngine templateEngine;

    private static final String MESSAGE_SUBJECT = "Bill for period: ";
    private static final String EMAIL_FROM = "someemail@email.com";
    private static final String EMAIL_TEMPLATE_ENCODING = "UTF-8";

    @Autowired
    public MailServiceImpl(JavaMailSender javaMailSender,
                           TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmail(final List<Domain> domains, final DateTime from, final DateTime to) {
        domains.forEach(domain -> {
            final Context context = new Context();
            context.setVariable("domain", domain.getName());
            final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, EMAIL_TEMPLATE_ENCODING);
            final String htmlContent;
            try {
                htmlContent = this.templateEngine.process("email-sample", context);
                messageHelper.setSubject(MESSAGE_SUBJECT + from.toString() + " to " + to.toString());
                messageHelper.setFrom(EMAIL_FROM);
                messageHelper.setTo(domain.getEmail());
                messageHelper.setText(htmlContent, true);
            } catch (MessagingException e) {
                throw new UnableToSendEmailException(e.getMessage(), e);
            }
            this.javaMailSender.send(mimeMessage);
        });
    }
}
