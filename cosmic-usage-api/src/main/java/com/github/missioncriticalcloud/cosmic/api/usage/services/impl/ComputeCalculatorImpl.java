package com.github.missioncriticalcloud.cosmic.api.usage.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.github.missioncriticalcloud.cosmic.api.usage.services.AggregationCalculator;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Compute;
import com.github.missioncriticalcloud.cosmic.usage.core.model.DataUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.InstanceType;
import com.github.missioncriticalcloud.cosmic.usage.core.model.TimeUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.VirtualMachine;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.VirtualMachinesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("computeCalculator")
public class ComputeCalculatorImpl implements AggregationCalculator<DomainAggregation> {

    private final VirtualMachinesRepository virtualMachinesRepository;

    @Autowired
    public ComputeCalculatorImpl(final VirtualMachinesRepository virtualMachinesRepository) {
        this.virtualMachinesRepository = virtualMachinesRepository;
    }

    @Override
    public void calculateAndMerge(
            final Domain domain,
            final BigDecimal secondsPerSample,
            final DataUnit dataUnit,
            final TimeUnit timeUnit,
            final List<DomainAggregation> aggregations) {
        aggregations.forEach(domainAggregation -> {
            final Compute compute = domain.getUsage().getCompute();

            domainAggregation.getVirtualMachineAggregations().forEach(virtualMachineAggregation -> {

                final VirtualMachine virtualMachine = virtualMachinesRepository.get(virtualMachineAggregation.getUuid());
                if (virtualMachine == null) {
                    return;
                }

                virtualMachineAggregation.getInstanceTypeAggregations().forEach(instanceTypeAggregation -> {

                    final InstanceType instanceType = new InstanceType();
                    instanceType.setCpu(instanceTypeAggregation.getCpu());

                    final BigDecimal memory = dataUnit.convert(instanceTypeAggregation.getMemory());
                    instanceType.setMemory(memory);

                    final BigDecimal duration = timeUnit.convert(instanceTypeAggregation.getCount().multiply(secondsPerSample));
                    instanceType.setDuration(duration);

                    virtualMachine.getInstanceTypes().add(instanceType);

                    final Optional<InstanceType> instanceTypeOptional = compute.getInstanceTypes()
                                                                               .stream()
                                                                               .filter(totalInstanceType ->
                                                                                       totalInstanceType.getCpu().equals(instanceType.getCpu()) &&
                                                                                               totalInstanceType.getMemory().equals(instanceType.getMemory())
                                                                               )
                                                                               .findFirst();

                    if (instanceTypeOptional.isPresent()) {
                        final InstanceType totalInstanceType = compute.getInstanceTypes().get(compute.getInstanceTypes().indexOf(instanceTypeOptional.get()));
                        totalInstanceType.setDuration(totalInstanceType.getDuration().add(instanceType.getDuration()));
                    } else {
                        final InstanceType totalInstanceType = new InstanceType();
                        totalInstanceType.setCpu(instanceType.getCpu());
                        totalInstanceType.setMemory(instanceType.getMemory());
                        totalInstanceType.setDuration(instanceType.getDuration());

                        compute.getInstanceTypes().add(totalInstanceType);
                    }
                });

                compute.getVirtualMachines().add(virtualMachine);
            });
        });
    }
}
