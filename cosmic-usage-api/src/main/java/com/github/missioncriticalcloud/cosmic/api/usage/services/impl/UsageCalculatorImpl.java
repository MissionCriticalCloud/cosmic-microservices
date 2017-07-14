package com.github.missioncriticalcloud.cosmic.api.usage.services.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.missioncriticalcloud.cosmic.api.usage.exceptions.NoMetricsFoundException;
import com.github.missioncriticalcloud.cosmic.api.usage.repositories.DomainsRepository;
import com.github.missioncriticalcloud.cosmic.api.usage.repositories.MetricsRepository;
import com.github.missioncriticalcloud.cosmic.api.usage.services.AggregationCalculator;
import com.github.missioncriticalcloud.cosmic.api.usage.services.UsageCalculator;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Report;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Unit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;

@Service
public class UsageCalculatorImpl implements UsageCalculator {
    private final DomainsRepository domainsRepository;

    private final MetricsRepository computeRepository;
    private final MetricsRepository storageRepository;
    private final MetricsRepository networkingRepository;

    private final AggregationCalculator<DomainAggregation> computeCalculator;
    private final AggregationCalculator<DomainAggregation> storageCalculator;
    private final AggregationCalculator<DomainAggregation> networkingCalculator;

    private final String scanInterval;

    @Autowired
    public UsageCalculatorImpl(
            final DomainsRepository domainsRepository,
            @Qualifier("computeRepository") final MetricsRepository computeRepository,
            @Qualifier("storageRepository") final MetricsRepository storageRepository,
            @Qualifier("networkingRepository") final MetricsRepository networkingRepository,
            @Qualifier("computeCalculator") final AggregationCalculator<DomainAggregation> computeCalculator,
            @Qualifier("storageCalculator") final AggregationCalculator<DomainAggregation> storageCalculator,
            @Qualifier("networkingCalculator") final AggregationCalculator<DomainAggregation> networkingCalculator,
            @Value("${cosmic.usage-api.scan-interval}") final String scanInterval
    ) {
        this.domainsRepository = domainsRepository;

        this.computeRepository = computeRepository;
        this.storageRepository = storageRepository;
        this.networkingRepository = networkingRepository;

        this.computeCalculator = computeCalculator;
        this.storageCalculator = storageCalculator;
        this.networkingCalculator = networkingCalculator;

        this.scanInterval = scanInterval;
    }

    @Override
    public Report calculate(
            final DateTime from,
            final DateTime to,
            final String path,
            final Unit unit,
            final boolean detailed
    ) {
        final Map<String, Domain> domainsMap = domainsRepository.map(path);
        final Set<String> domainUuids = domainsMap.keySet();

        final List<DomainAggregation> computeDomainAggregations = computeRepository.list(domainUuids, from, to);
        final List<DomainAggregation> storageDomainAggregations = storageRepository.list(domainUuids, from, to);
        final List<DomainAggregation> networkingDomainAggregations = networkingRepository.list(domainUuids, from, to);

        final BigDecimal expectedSampleCount = calculateExpectedSampleCount(from, to);

        computeCalculator.calculateAndMerge(domainsMap, expectedSampleCount, unit, computeDomainAggregations, detailed);
        storageCalculator.calculateAndMerge(domainsMap, expectedSampleCount, unit, storageDomainAggregations, detailed);
        networkingCalculator.calculateAndMerge(domainsMap, expectedSampleCount, unit, networkingDomainAggregations, detailed);
        removeDomainsWithoutUsage(domainsMap);

        if (domainsMap.isEmpty()) {
            throw new NoMetricsFoundException();
        }

        final Report report = new Report();
        report.getDomains().addAll(domainsMap.values());

        return report;
    }

    private BigDecimal calculateExpectedSampleCount(final DateTime from, final DateTime to) {
        final Duration duration = new Duration(from, to);
        final BigDecimal durationInSeconds = BigDecimal.valueOf(duration.getStandardSeconds());

        final CronSequenceGenerator cronSequence = new CronSequenceGenerator(scanInterval);
        final Date nextOccurrence = cronSequence.next(new Date());
        final Date followingOccurrence = cronSequence.next(nextOccurrence);

        final Duration interval = new Duration(nextOccurrence.getTime(), followingOccurrence.getTime());
        final BigDecimal intervalInSeconds = BigDecimal.valueOf(interval.getStandardSeconds());

        return durationInSeconds.divide(intervalInSeconds, RoundingMode.UNNECESSARY);
    }

    private void removeDomainsWithoutUsage(final Map<String, Domain> domainsMap) {
        final Set<String> uuidsToRemove = new HashSet<>();
        domainsMap.forEach((uuid, domain) -> {
            if (domain.getUsage().isEmpty()) {
                uuidsToRemove.add(uuid);
            }
        });
        uuidsToRemove.forEach(domainsMap::remove);
    }
}
