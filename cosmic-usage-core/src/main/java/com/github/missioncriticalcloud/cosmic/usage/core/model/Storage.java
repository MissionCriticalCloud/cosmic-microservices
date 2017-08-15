package com.github.missioncriticalcloud.cosmic.usage.core.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.GeneralView;

public class Storage {

    @JsonView(DetailedView.class)
    private List<Volume> volumes = new LinkedList<>();

    @JsonView({GeneralView.class, DetailedView.class})
    private List<VolumeConfiguration> total = new LinkedList<>();

    public List<Volume> getVolumes() {
        return volumes;
    }

    public void setVolumes(final List<Volume> volumes) {
        this.volumes = volumes;
    }

    public List<VolumeConfiguration> getTotal() {
        return total;
    }

    public void setTotal(final List<VolumeConfiguration> total) {
        this.total = total;
    }
}
