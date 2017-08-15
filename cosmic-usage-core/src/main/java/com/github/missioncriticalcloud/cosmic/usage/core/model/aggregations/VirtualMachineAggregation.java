package com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations;

import java.util.LinkedList;
import java.util.List;

public class VirtualMachineAggregation extends ResourceAggregation {

    private List<VirtualMachineConfigurationAggregation> virtualMachineConfigurationAggregations = new LinkedList<>();

    public List<VirtualMachineConfigurationAggregation> getVirtualMachineConfigurationAggregations() {
        return virtualMachineConfigurationAggregations;
    }

    public void setVirtualMachineConfigurationAggregations(final List<VirtualMachineConfigurationAggregation> virtualMachineConfigurationAggregations) {
        this.virtualMachineConfigurationAggregations = virtualMachineConfigurationAggregations;
    }
}
