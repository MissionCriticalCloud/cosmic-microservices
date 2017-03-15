package com.github.missioncriticalcloud.cosmic.metricscollector.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ResourceType {

    VIRTUAL_MACHINE("VirtualMachine"),
    PUBLIC_IP_ADDRESS("PublicIPAddress");

    private final String value;

    ResourceType(final String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

}
