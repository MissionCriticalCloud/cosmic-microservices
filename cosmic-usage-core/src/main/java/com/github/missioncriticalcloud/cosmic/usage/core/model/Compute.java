package com.github.missioncriticalcloud.cosmic.usage.core.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.ComputeView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;

public class Compute {

    @JsonView({DetailedView.class, ComputeView.class})
    private List<VirtualMachine> virtualMachines = new LinkedList<>();

    public List<VirtualMachine> getVirtualMachines() {
        return virtualMachines;
    }

}
