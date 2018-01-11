package com.github.missioncriticalcloud.cosmic.usage.core.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.model.types.NetworkType;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.NetworkingView;

public class Network extends Resource {

    @JsonView({DetailedView.class, NetworkingView.class})
    private String name;

    @JsonView({DetailedView.class, NetworkingView.class})
    private NetworkType type;

    @JsonView({DetailedView.class, NetworkingView.class})
    private List<PublicIp> publicIps = new LinkedList<>();

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public NetworkType getType() {
        return type;
    }

    public void setType(final NetworkType type) {
        this.type = type;
    }

    public List<PublicIp> getPublicIps() {
        return publicIps;
    }

}
