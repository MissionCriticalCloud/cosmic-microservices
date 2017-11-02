package com.github.missioncriticalcloud.cosmic.api.usage.services;

import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.model.DataUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.TimeUnit;
import org.joda.time.DateTime;

public interface UsageCalculator {

    Domain calculate(DateTime from, DateTime to, String path, DataUnit dataUnit, TimeUnit timeUnit);

    List<Domain> calculateComputeDomains(DateTime from, DateTime to, String path, DataUnit dataUnit, TimeUnit timeUnit);

    Domain calculateComputeForUuid(DateTime from, DateTime to, Domain domain, DataUnit dataUnit, TimeUnit timeUnit);

    List<Domain> calculateStorageDomains(DateTime from, DateTime to, String path, DataUnit dataUnit, TimeUnit timeUnit);

    Domain calculateStorageForUuid(DateTime from, DateTime to, Domain domain, DataUnit dataUnit, TimeUnit timeUnit);

}
