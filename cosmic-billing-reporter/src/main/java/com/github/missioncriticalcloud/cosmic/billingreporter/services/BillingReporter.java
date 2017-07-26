package com.github.missioncriticalcloud.cosmic.billingreporter.services;

import org.joda.time.DateTime;

public interface BillingReporter {

    void createReport(DateTime from, DateTime to);

}
