package com.github.missioncriticalcloud.cosmic.billingreporter.services.impl;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.MONTH_DATE_FORMATTER;

import java.util.ArrayList;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.billingreporter.services.BillingReporter;
import com.github.missioncriticalcloud.cosmic.billingreporter.services.MailComponent;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.DomainsMetricsRepository;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.DomainsRepository;
import org.joda.time.DateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BillingReporterImpl implements BillingReporter {

    private DomainsMetricsRepository domainsMetricsRepository;
    private DomainsRepository domainsRepository;
    private MailComponent mailComponent;

    public BillingReporterImpl(final DomainsMetricsRepository domainsMetricsRepository, final DomainsRepository domainsRepository, final MailComponent mailComponent) {
        this.domainsMetricsRepository = domainsMetricsRepository;
        this.domainsRepository = domainsRepository;
        this.mailComponent = mailComponent;
    }

    private List<Domain> getBillableDomains(final DateTime from, final DateTime to) {
        List<String> domainUuids = domainsMetricsRepository.listMeasuredDomains(from, to);

        List<Domain> domains = new ArrayList<>();

        domainUuids.forEach(uuid -> domains.add(domainsRepository.get(uuid)));

        return domains;
    }

    @Override
    public void createReport(final DateTime from, final DateTime to) {
        mailComponent.sendEmail(getBillableDomains(from, to), from, to);
    }

    @Scheduled(cron = "${cosmic.billing-reporter.scan-interval}")
    public void report() {
        final DateTime current = DateTime.now();
        final DateTime next = current.plusMonths(1);

        final DateTime currentBillableMonth = MONTH_DATE_FORMATTER.parseDateTime(current.getYear() + "-" + current.getMonthOfYear());
        final DateTime nextBillableMonth = MONTH_DATE_FORMATTER.parseDateTime( next.getYear() + "-" + next.getMonthOfYear());
        createReport(currentBillableMonth, nextBillableMonth);
    }

}
