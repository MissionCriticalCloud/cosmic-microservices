package com.github.missioncriticalcloud.cosmic.billingreporter.services;

import javax.mail.internet.MimeMessage;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import org.joda.time.DateTime;

public interface MailService {

    void sendEmail(List<Domain> domains, DateTime from, DateTime to);

    MimeMessage getMimeMessage(DateTime from, DateTime to, Domain domain);

}
