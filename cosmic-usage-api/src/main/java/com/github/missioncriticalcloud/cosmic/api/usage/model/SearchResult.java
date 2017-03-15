package com.github.missioncriticalcloud.cosmic.api.usage.model;

import static com.github.missioncriticalcloud.cosmic.api.usage.utils.FormatUtils.DEFAULT_ROUNDING_MODE;
import static com.github.missioncriticalcloud.cosmic.api.usage.utils.FormatUtils.DEFAULT_SCALE;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class SearchResult {

    private BigDecimal sampleCount = BigDecimal.ZERO;
    private final List<Domain> domains = new LinkedList<>();

    public SearchResult() {
        // Empty constructor
    }

    public SearchResult(final BigDecimal sampleCount) {
        setSampleCount(sampleCount);
    }

    public BigDecimal getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(final BigDecimal sampleCount) {
        this.sampleCount = sampleCount.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    public List<Domain> getDomains() {
        return domains;
    }

}
