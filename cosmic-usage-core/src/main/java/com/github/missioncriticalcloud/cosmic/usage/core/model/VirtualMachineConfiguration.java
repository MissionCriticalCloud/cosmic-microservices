package com.github.missioncriticalcloud.cosmic.usage.core.model;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DEFAULT_ROUNDING_MODE;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DEFAULT_SCALE;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.GeneralView;

public class VirtualMachineConfiguration {

    @JsonView({GeneralView.class, DetailedView.class})
    private BigDecimal cpu = BigDecimal.ZERO;

    @JsonView({GeneralView.class, DetailedView.class})
    private BigDecimal memory = BigDecimal.ZERO;

    @JsonView({GeneralView.class, DetailedView.class})
    private BigDecimal duration = BigDecimal.ZERO;

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

    public BigDecimal getDuration() {
        return duration;
    }

    public void setDuration(final BigDecimal duration) {
        this.duration = duration;
    }
}
