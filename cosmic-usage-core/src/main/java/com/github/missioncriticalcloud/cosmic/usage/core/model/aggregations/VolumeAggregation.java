package com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations;

import java.util.LinkedList;
import java.util.List;

public class VolumeAggregation extends ResourceAggregation {

    private List<VolumeConfigurationAggregation> volumeConfigurationAggregations = new LinkedList<>();

    public List<VolumeConfigurationAggregation> getVolumeConfigurationAggregations() {
        return volumeConfigurationAggregations;
    }

    public void setVolumeConfigurationAggregations(final List<VolumeConfigurationAggregation> volumeConfigurationAggregations) {
        this.volumeConfigurationAggregations = volumeConfigurationAggregations;
    }
}
