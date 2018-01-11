package com.github.missioncriticalcloud.cosmic.usage.core.repositories;

import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import org.joda.time.DateTime;

public interface MetricsRepository {

    DomainAggregation getDomainAggregation(String domainUuid, DateTime from, DateTime to);
}
