package com.github.missioncriticalcloud.cosmic.usage.core.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.ComputeView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.GeneralView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.NetworkingView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.StorageView;

public class Domain {

    public static final String ROOT_PATH = "/";

    @JsonView({GeneralView.class, DetailedView.class, ComputeView.class, StorageView.class, NetworkingView.class})
    private String uuid;

    @JsonView({GeneralView.class, DetailedView.class, ComputeView.class, StorageView.class, NetworkingView.class})
    private String name;

    @JsonView({GeneralView.class, DetailedView.class, ComputeView.class, StorageView.class, NetworkingView.class})
    private String path;

    @JsonView({DetailedView.class, ComputeView.class, StorageView.class, NetworkingView.class})
    private Usage usage = new Usage();

    private String email;

    public Domain(final String uuid) {
        setUuid(uuid);
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

    public Usage getUsage() {
        return usage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
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
