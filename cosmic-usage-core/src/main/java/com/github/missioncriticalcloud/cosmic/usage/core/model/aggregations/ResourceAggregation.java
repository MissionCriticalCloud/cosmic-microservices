package com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations;

import java.math.BigDecimal;

public abstract class ResourceAggregation {

    private String uuid;

    private BigDecimal count = BigDecimal.ZERO;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(final BigDecimal count) {
        this.count = count;
    }
}
