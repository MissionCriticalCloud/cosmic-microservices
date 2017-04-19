package com.github.missioncriticalcloud.cosmic.usage.core.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;

public abstract class Resource {

    @JsonView(DetailedView.class)
    private String uuid;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Domain)) {
            return false;
        }

        final Domain domain = (Domain) o;

        return Objects.equals(getUuid(), domain.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUuid());
    }

}
