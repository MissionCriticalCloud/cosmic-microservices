package com.github.missioncriticalcloud.cosmic.billingreporter.services.impl;

import com.github.missioncriticalcloud.cosmic.billingreporter.repositories.DomainsRepository;
import com.github.missioncriticalcloud.cosmic.billingreporter.services.BillingReporter;
import org.springframework.stereotype.Service;

@Service
public class BillingReporterImpl implements BillingReporter {

    private DomainsRepository domainsRepository;

    public BillingReporterImpl(final DomainsRepository domainsRepository) {
        this.domainsRepository = domainsRepository;
    }

    @Override
    public void createReport() {

    }
}
