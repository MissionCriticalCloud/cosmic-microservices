package com.github.missioncriticalcloud.cosmic.usage.core.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.GeneralView;

public class Usage {

    @JsonView({GeneralView.class, DetailedView.class})
    private Compute compute = new Compute();

    @JsonView({GeneralView.class, DetailedView.class})
    private Storage storage = new Storage();

    @JsonView({GeneralView.class, DetailedView.class})
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
                storage.getTotal().isEmpty() &&
                networking.getTotal().getPublicIps().compareTo(BigDecimal.ZERO) == 0;
    }
}
