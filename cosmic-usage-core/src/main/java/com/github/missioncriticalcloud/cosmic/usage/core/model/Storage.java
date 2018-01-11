package com.github.missioncriticalcloud.cosmic.usage.core.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.StorageView;

public class Storage {

    @JsonView({DetailedView.class, StorageView.class})
    private List<Volume> volumes = new LinkedList<>();

    public List<Volume> getVolumes() {
        return volumes;
    }

    public void setVolumes(final List<Volume> volumes) {
        this.volumes = volumes;
    }

}
