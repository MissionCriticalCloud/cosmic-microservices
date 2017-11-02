package com.github.missioncriticalcloud.cosmic.usage.core.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.ComputeView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.GeneralView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.NetworkingView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.StorageView;

public class Usage {

    @JsonView({GeneralView.class, DetailedView.class, ComputeView.class})
    private Compute compute = new Compute();

    @JsonView({GeneralView.class, DetailedView.class, StorageView.class})
    private Storage storage = new Storage();

    @JsonView({GeneralView.class, DetailedView.class, NetworkingView.class})
    private Networking networking = new Networking();

    public Compute getCompute() {
        return compute;
    }

    public Storage getStorage() {
        return storage;
    }

    public Networking getNetworking() {
        return networking;
    }

    public boolean isEmpty() {
        return compute.getInstanceTypes().isEmpty() &&
                storage.getVolumeSizes().isEmpty() &&
                networking.getNetworks().isEmpty();
    }
}
