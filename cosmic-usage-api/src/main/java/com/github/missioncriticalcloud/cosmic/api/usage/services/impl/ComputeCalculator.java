package com.github.missioncriticalcloud.cosmic.api.usage.services.impl;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DEFAULT_ROUNDING_MODE;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.github.missioncriticalcloud.cosmic.api.usage.services.AggregationCalculator;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Compute;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Unit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.VirtualMachine;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.VirtualMachinesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("computeCalculator")
public class ComputeCalculator implements AggregationCalculator<DomainAggregation> {

    private final VirtualMachinesRepository virtualMachinesRepository;

    @Autowired
    public ComputeCalculator(final VirtualMachinesRepository virtualMachinesRepository) {
        this.virtualMachinesRepository = virtualMachinesRepository;
    }

    @Override
    public void calculateAndMerge(
            final Map<String, Domain> domainsMap,
            final BigDecimal expectedSampleCount,
            final Unit unit,
            final List<DomainAggregation> aggregations,
            final boolean detailed
    ) {
        aggregations.forEach(domainAggregation -> {
            final String domainAggregationUuid = domainAggregation.getUuid();
            final Domain domain = domainsMap.getOrDefault(domainAggregationUuid, new Domain(domainAggregationUuid));

            final Compute compute = domain.getUsage().getCompute();
            final Compute.Total total = compute.getTotal();

            domainAggregation.getVirtualMachineAggregations().forEach(virtualMachineAggregation -> {
                final BigDecimal cpu = virtualMachineAggregation.getCpuAverage()
                                                                .multiply(virtualMachineAggregation.getSampleCount())
                                                                .divide(expectedSampleCount, DEFAULT_ROUNDING_MODE);
                final BigDecimal memory = unit.convert(
                        virtualMachineAggregation.getMemoryAverage()
                                                 .multiply(virtualMachineAggregation.getSampleCount())
                                                 .divide(expectedSampleCount, DEFAULT_ROUNDING_MODE)
                );

                if (detailed) {
                    final VirtualMachine virtualMachine = virtualMachinesRepository.get(virtualMachineAggregation.getUuid());
                    if (virtualMachine != null) {
                        virtualMachine.setCpu(cpu);
                        virtualMachine.setMemory(memory);
                        compute.getVirtualMachines().add(virtualMachine);
                    }
                }

                total.addCpu(cpu);
                total.addMemory(memory);
            });

            domainsMap.put(domainAggregation.getUuid(), domain);
        });
    }
}
