package com.github.missioncriticalcloud.cosmic.billingreporter.services;

import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import org.joda.time.DateTime;

public interface BillingReporter {

    void createReport(DateTime from, DateTime to);

    List<Domain> getBillableDomains(DateTime from, DateTime to);
}
