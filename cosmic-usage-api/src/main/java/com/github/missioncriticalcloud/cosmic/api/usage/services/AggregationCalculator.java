package com.github.missioncriticalcloud.cosmic.api.usage.services;

import java.math.BigDecimal;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.model.DataUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.TimeUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.ResourceAggregation;

public interface AggregationCalculator<T extends ResourceAggregation> {

    void calculateAndMerge(
            Domain domain,
            BigDecimal expectedSampleCount,
            DataUnit dataUnit,
            TimeUnit timeUnit,
            List<T> aggregations
    );

}
