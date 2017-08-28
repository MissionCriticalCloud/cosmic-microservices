package com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations;

import java.util.LinkedList;
import java.util.List;

public class VirtualMachineAggregation extends ResourceAggregation {

    private List<InstanceTypeAggregation> instanceTypeAggregations = new LinkedList<>();

    public List<InstanceTypeAggregation> getInstanceTypeAggregations() {
        return instanceTypeAggregations;
    }

    public void setInstanceTypeAggregations(final List<InstanceTypeAggregation> instanceTypeAggregations) {
        this.instanceTypeAggregations = instanceTypeAggregations;
    }
}
