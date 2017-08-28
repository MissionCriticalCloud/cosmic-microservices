package com.github.missioncriticalcloud.cosmic.usage.core.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;

public class Volume extends Resource {

    @JsonView(DetailedView.class)
    private String name;

    @JsonView(DetailedView.class)
    private List<VolumeSize> volumeSizes = new LinkedList<>();

    @JsonView(DetailedView.class)
    private String attachedTo;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public List<VolumeSize> getVolumeSizes() {
        return volumeSizes;
    }

    public void setVolumeSizes(final List<VolumeSize> volumeSizes) {
        this.volumeSizes = volumeSizes;
    }

    public void setAttachedTo(final String attachedTo) {
        this.attachedTo = attachedTo;
    }

    public String getAttachedTo() {
        return attachedTo;
    }
}
