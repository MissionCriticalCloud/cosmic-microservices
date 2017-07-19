package com.github.missioncriticalcloud.cosmic.billingreporter.services.impl;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;

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

    private static String MESSAGE_SUBJECT = "Bill for period: ";

    @Autowired
    public MailServiceImpl(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void sendEmail(final List<Domain> domains, final DateTime from, final DateTime to) {
        domains.forEach(domain -> {
            final Context context = new Context();
            context.setVariable("domain", domain.getName());
            final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
            final MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            final String htmlContent = this.templateEngine.process("template_location", context);

            try {
                messageHelper.setSubject(MESSAGE_SUBJECT + from.toString() + "to" + to.toString());
                messageHelper.setFrom("someemail@email.com");
                messageHelper.setTo(domain.getEmail());
                messageHelper.setText(htmlContent, true);
            } catch (MessagingException e) {
                e.printStackTrace(); //TODO handle this exception somehow
            }
        });
    }
}
