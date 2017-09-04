package com.github.missioncriticalcloud.cosmic.usage.core.model;

import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.GeneralView;

public class VolumeSize {

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

    @Override
    public int hashCode() {
        return Objects.hash(getSize());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VolumeSize)) {
            return false;
        }
        final VolumeSize that = (VolumeSize) o;
        return Objects.equals(getSize(), that.getSize());
    }
}
