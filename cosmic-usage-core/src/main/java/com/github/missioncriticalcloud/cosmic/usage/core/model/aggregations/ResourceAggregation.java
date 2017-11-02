package com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DEFAULT_ROUNDING_MODE;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DEFAULT_SCALE;

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
        return count.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    public void setCount(final BigDecimal count) {
        this.count = count;
    }
}
