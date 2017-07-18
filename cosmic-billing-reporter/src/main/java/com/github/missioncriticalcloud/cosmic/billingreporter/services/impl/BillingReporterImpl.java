package com.github.missioncriticalcloud.cosmic.billingreporter.services.impl;

import com.github.missioncriticalcloud.cosmic.billingreporter.services.BillingReporter;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.DomainsEsRepository;
import org.springframework.stereotype.Service;

@Service
public class BillingReporterImpl implements BillingReporter {

    private DomainsEsRepository domainsEsRepository;

    public BillingReporterImpl(final DomainsEsRepository domainsEsRepository) {
        this.domainsEsRepository = domainsEsRepository;
    }

    @Override
    public void createReport() {

    }
}
