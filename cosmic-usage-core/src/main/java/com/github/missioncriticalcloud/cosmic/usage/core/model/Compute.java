package com.github.missioncriticalcloud.cosmic.usage.core.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.DetailedView;
import com.github.missioncriticalcloud.cosmic.usage.core.views.GeneralView;

public class Compute {

    @JsonView(DetailedView.class)
    private List<VirtualMachine> virtualMachines = new LinkedList<>();

    @JsonView({GeneralView.class, DetailedView.class})
    private List<VirtualMachineConfiguration> total = new LinkedList<>();

    public List<VirtualMachine> getVirtualMachines() {
        return virtualMachines;
    }

    public List<VirtualMachineConfiguration> getTotal() {
        return total;
    }
}
