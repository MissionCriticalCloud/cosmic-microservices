package com.github.missioncriticalcloud.cosmic.api.usage.model;

import static com.github.missioncriticalcloud.cosmic.api.usage.utils.FormatUtils.DEFAULT_ROUNDING_MODE;
import static com.github.missioncriticalcloud.cosmic.api.usage.utils.FormatUtils.DEFAULT_SCALE;

import java.math.BigDecimal;

public class State {

    public static final String RUNNING = "Running";
    public static final String STOPPED = "Stopped";

    private String value;
    private BigDecimal sampleCount = BigDecimal.ZERO;
    private BigDecimal cpuAverage = BigDecimal.ZERO;
    private BigDecimal memoryAverage = BigDecimal.ZERO;

    public State() {
        // Empty constructor
    }

    public State(
            final String value,
            final BigDecimal sampleCount,
            final BigDecimal cpuAverage,
            final BigDecimal memoryAverage
    ) {
        setValue(value);
        setSampleCount(sampleCount);
        setCpuAverage(cpuAverage);
        setMemoryAverage(memoryAverage);
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public BigDecimal getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(final BigDecimal sampleCount) {
        this.sampleCount = sampleCount.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    public BigDecimal getCpuAverage() {
        return cpuAverage;
    }

    public void setCpuAverage(final BigDecimal cpuAverage) {
        this.cpuAverage = cpuAverage.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    public BigDecimal getMemoryAverage() {
        return memoryAverage;
    }

    public void setMemoryAverage(final BigDecimal memoryAverage) {
        this.memoryAverage = memoryAverage.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

}
