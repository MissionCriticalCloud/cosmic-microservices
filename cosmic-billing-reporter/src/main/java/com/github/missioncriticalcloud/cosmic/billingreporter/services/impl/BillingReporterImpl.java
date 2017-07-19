package com.github.missioncriticalcloud.cosmic.billingreporter.services.impl;

import java.util.ArrayList;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.billingreporter.services.BillingReporter;
import com.github.missioncriticalcloud.cosmic.billingreporter.services.MailService;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.DomainsEsRepository;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.DomainsRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingReporterImpl implements BillingReporter {

    private DomainsEsRepository domainsEsRepository;
    private DomainsRepository domainsRepository;
    private MailService mailService;

    public BillingReporterImpl(final DomainsEsRepository domainsEsRepository, final DomainsRepository domainsRepository, final MailService mailService) {
        this.domainsEsRepository = domainsEsRepository;
        this.domainsRepository = domainsRepository;
        this.mailService = mailService;
    }

    @Override
    public List<Domain> getBillableDomains(final DateTime from, final DateTime to) {
        List<String> domainUuids = domainsEsRepository.listDomains(from, to);

        List<Domain> domains = new ArrayList<>();

        domainUuids.forEach(uuid -> domains.add(domainsRepository.getByUuid(uuid)));

        return domains;
    }

    @Override
    public void createReport(final DateTime from, final DateTime to) {
        mailService.sendEmail(getBillableDomains(from, to), from, to);
    }
}
