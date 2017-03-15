package com.github.missioncriticalcloud.cosmic.api.usage.model;

import static com.github.missioncriticalcloud.cosmic.api.usage.utils.FormatUtils.DEFAULT_ROUNDING_MODE;
import static com.github.missioncriticalcloud.cosmic.api.usage.utils.FormatUtils.DEFAULT_SCALE;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Domain {

    private String uuid;
    private String name;
    private String path;

    @JsonIgnore
    private BigDecimal sampleCount = BigDecimal.ZERO;

    @JsonIgnore
    private final List<Resource> resources = new LinkedList<>();

    public Domain() {
        // Empty constructor
    }

    public Domain(final String uuid) {
        setUuid(uuid);
    }

    public Domain(
            final String uuid,
            final String name,
            final String path,
            final BigDecimal sampleCount
    ) {
        setUuid(uuid);
        setName(name);
        setPath(path);
        setSampleCount(sampleCount);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public BigDecimal getSampleCount() {
        return sampleCount;
    }

    public void setSampleCount(final BigDecimal sampleCount) {
        this.sampleCount = sampleCount.setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

    public List<Resource> getResources() {
        return resources;
    }

}
