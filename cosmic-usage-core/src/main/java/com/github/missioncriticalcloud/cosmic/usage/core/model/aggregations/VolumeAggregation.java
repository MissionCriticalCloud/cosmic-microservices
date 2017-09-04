package com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations;

import java.util.LinkedList;
import java.util.List;

public class VolumeAggregation extends ResourceAggregation {

    private List<VolumeSizeAggregation> volumeSizeAggregations = new LinkedList<>();

    public List<VolumeSizeAggregation> getVolumeSizeAggregations() {
        return volumeSizeAggregations;
    }

    public void setVolumeSizeAggregations(final List<VolumeSizeAggregation> volumeSizeAggregations) {
        this.volumeSizeAggregations = volumeSizeAggregations;
    }
}
