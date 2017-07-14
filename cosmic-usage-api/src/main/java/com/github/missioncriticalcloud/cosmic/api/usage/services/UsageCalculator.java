package com.github.missioncriticalcloud.cosmic.api.usage.services;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Report;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Unit;
import org.joda.time.DateTime;

public interface UsageCalculator {

    Report calculate(DateTime from, DateTime to, String path, final Unit unit, boolean detailed);
}
