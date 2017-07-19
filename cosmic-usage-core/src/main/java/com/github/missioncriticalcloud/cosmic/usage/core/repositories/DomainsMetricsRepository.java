package com.github.missioncriticalcloud.cosmic.usage.core.repositories;

import java.util.List;

import org.joda.time.DateTime;

public interface DomainsMetricsRepository {

    List<String> listMeasuredDomains(final DateTime from, final DateTime to);
}
