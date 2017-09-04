package com.github.missioncriticalcloud.cosmic.usage.core.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.model.types.OsType;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;

public class VirtualMachine extends Resource {

    @JsonView(DetailedView.class)
    private String hostname;

    @JsonView(DetailedView.class)
    private OsType osType;

    @JsonView(DetailedView.class)
    private List<InstanceType> instanceTypes = new LinkedList<>();

    public String getHostname() {
        return hostname;
    }

    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }

    public OsType getOsType() {
        return osType;
    }

    public void setOsType(final OsType osType) {
        this.osType = osType;
    }

    public List<InstanceType> getInstanceTypes() {
        return instanceTypes;
    }

    public void setInstanceTypes(final List<InstanceType> instanceTypes) {
        this.instanceTypes = instanceTypes;
    }
}
