package com.github.missioncriticalcloud.cosmic.usage.core.model;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DEFAULT_ROUNDING_MODE;

import java.math.BigDecimal;

public enum DataUnit {

    BYTES(1),
    KB(1024),
    MB(1024 * 1024),
    GB(1024 * 1024 * 1024);
    public static final String DEFAULT = "BYTES";

    private final BigDecimal factor;

    DataUnit(final int factor) {
        this.factor = BigDecimal.valueOf(factor);
    }

    public BigDecimal convert(final BigDecimal number) {
        return number.divide(factor, DEFAULT_ROUNDING_MODE);
    }

}
