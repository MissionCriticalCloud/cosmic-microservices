package com.github.missioncriticalcloud.cosmic.usage.core.model;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DEFAULT_ROUNDING_MODE;

import java.math.BigDecimal;

public enum TimeUnit {

    SECONDS(1),
    MINUTES(60),
    HOURS(60 * 60),
    DAYS(60 * 60 * 24);
    public static final String DEFAULT = "SECONDS";

    private final BigDecimal factor;

    TimeUnit(final int factor) {
        this.factor = BigDecimal.valueOf(factor);
    }

    public BigDecimal convert(final BigDecimal number) {
        return number.divide(factor, DEFAULT_ROUNDING_MODE);
    }

}
