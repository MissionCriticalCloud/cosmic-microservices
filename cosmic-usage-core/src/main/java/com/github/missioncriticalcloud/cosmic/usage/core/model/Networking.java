package com.github.missioncriticalcloud.cosmic.usage.core.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.NetworkingView;

public class Networking {

    @JsonView({DetailedView.class, NetworkingView.class})
    private List<Network> networks = new LinkedList<>();

    public List<Network> getNetworks() {
        return networks;
    }
}
