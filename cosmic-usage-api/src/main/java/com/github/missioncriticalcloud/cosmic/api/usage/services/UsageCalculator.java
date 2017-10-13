package com.github.missioncriticalcloud.cosmic.api.usage.services;

import com.github.missioncriticalcloud.cosmic.usage.core.model.DataUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.TimeUnit;
import org.joda.time.DateTime;

public interface UsageCalculator {

    Domain calculate(DateTime from, DateTime to, String path, DataUnit dataUnit, TimeUnit timeUnit);
}
