package com.github.missioncriticalcloud.cosmic.usage.core.repositories;

import java.util.List;
import java.util.Set;

import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import org.joda.time.DateTime;

public interface MetricsRepository {

    List<DomainAggregation> list(Set<String> domainUuids, DateTime from, DateTime to);
}
