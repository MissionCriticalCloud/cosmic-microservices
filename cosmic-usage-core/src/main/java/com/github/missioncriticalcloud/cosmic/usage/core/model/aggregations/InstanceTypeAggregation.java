package com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DEFAULT_ROUNDING_MODE;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DEFAULT_SCALE;

import java.math.BigDecimal;

public class InstanceTypeAggregation {

    private BigDecimal cpu = BigDecimal.ZERO;
    private BigDecimal memory = BigDecimal.ZERO;

    private BigDecimal count = BigDecimal.ZERO;

    public BigDecimal getCpu() {
        return cpu.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    public void setCpu(final BigDecimal cpu) {
        this.cpu = cpu;
    }

    public BigDecimal getMemory() {
        return memory.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    public void setMemory(final BigDecimal memory) {
        this.memory = memory;
    }

    public BigDecimal getCount() {
        return count;
    }

    public void setCount(final BigDecimal count) {
        this.count = count;
    }
}
