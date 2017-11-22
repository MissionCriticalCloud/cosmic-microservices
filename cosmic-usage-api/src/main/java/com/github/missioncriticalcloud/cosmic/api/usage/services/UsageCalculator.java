package com.github.missioncriticalcloud.cosmic.api.usage.services;

import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.model.DataUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.TimeUnit;
import org.joda.time.DateTime;

public interface UsageCalculator {

    List<Domain> calculateCompute(DateTime from, DateTime to, String path, DataUnit dataUnit, TimeUnit timeUnit);

    Domain calculateCompute(DateTime from, DateTime to, Domain domain, DataUnit dataUnit, TimeUnit timeUnit);

    List<Domain> calculateStorage(DateTime from, DateTime to, String path, DataUnit dataUnit, TimeUnit timeUnit);

    Domain calculateStorage(DateTime from, DateTime to, Domain domain, DataUnit dataUnit, TimeUnit timeUnit);

    List<Domain> calculateNetworking(DateTime from, DateTime to, String path, DataUnit dataUnit, TimeUnit timeUnit);

    Domain calculateNetworking(DateTime from, DateTime to, Domain domain, DataUnit dataUnit, TimeUnit timeUnit);

    Domain calculateDetailed(DateTime from, DateTime to, String path, DataUnit dataUnit, TimeUnit timeUnit);

}
