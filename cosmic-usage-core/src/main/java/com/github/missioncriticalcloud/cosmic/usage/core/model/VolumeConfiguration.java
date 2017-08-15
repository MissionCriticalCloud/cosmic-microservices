package com.github.missioncriticalcloud.cosmic.usage.core.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.GeneralView;

public class VolumeConfiguration {

    @JsonView({GeneralView.class, DetailedView.class})
    private BigDecimal size = BigDecimal.ZERO;

    @JsonView({GeneralView.class, DetailedView.class})
    private BigDecimal duration = BigDecimal.ZERO;

    public BigDecimal getSize() {
        return size;
    }

    public void setSize(final BigDecimal size) {
        this.size = size;
    }

    public BigDecimal getDuration() {
        return duration;
    }

    public void setDuration(final BigDecimal duration) {
        this.duration = duration;
    }
}
