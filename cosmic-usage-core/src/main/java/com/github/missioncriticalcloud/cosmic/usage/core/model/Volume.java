package com.github.missioncriticalcloud.cosmic.usage.core.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;

public class Volume extends Resource {

    @JsonView(DetailedView.class)
    private String name;

    @JsonView(DetailedView.class)
    private List<VolumeConfiguration> volumeConfigurations = new LinkedList<>();

    @JsonView(DetailedView.class)
    private String attachedTo;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<VolumeConfiguration> getVolumeConfigurations() {
        return volumeConfigurations;
    }

    public void setVolumeConfigurations(final List<VolumeConfiguration> volumeConfigurations) {
        this.volumeConfigurations = volumeConfigurations;
    }

    public void setAttachedTo(final String attachedTo) {
        this.attachedTo = attachedTo;
    }

    public String getAttachedTo() {
        return attachedTo;
    }
}
