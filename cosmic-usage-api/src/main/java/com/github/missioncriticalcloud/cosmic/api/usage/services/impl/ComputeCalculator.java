package com.github.missioncriticalcloud.cosmic.api.usage.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.missioncriticalcloud.cosmic.api.usage.services.AggregationCalculator;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Compute;
import com.github.missioncriticalcloud.cosmic.usage.core.model.DataUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.TimeUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.VirtualMachine;
import com.github.missioncriticalcloud.cosmic.usage.core.model.VirtualMachineConfiguration;
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
            final BigDecimal secondsPerSample,
            final DataUnit dataUnit,
            final TimeUnit timeUnit,
            final List<DomainAggregation> aggregations,
            final boolean detailed
    ) {
        aggregations.forEach(domainAggregation -> {
            final String domainAggregationUuid = domainAggregation.getUuid();
            final Domain domain = domainsMap.getOrDefault(domainAggregationUuid, new Domain(domainAggregationUuid));

            final Compute compute = domain.getUsage().getCompute();

            domainAggregation.getVirtualMachineAggregations().forEach(virtualMachineAggregation -> {

                final VirtualMachine virtualMachine = virtualMachinesRepository.get(virtualMachineAggregation.getUuid());

                if (virtualMachine != null) {
                    virtualMachineAggregation.getVirtualMachineConfigurationAggregations().forEach(virtualMachineConfigurationAggregation -> {

                        final VirtualMachineConfiguration virtualMachineConfiguration = new VirtualMachineConfiguration();

                        final BigDecimal cpu = virtualMachineConfigurationAggregation.getCpu();
                        final BigDecimal memory = dataUnit.convert(virtualMachineConfigurationAggregation.getMemory());
                        final BigDecimal duration = timeUnit.convert(virtualMachineConfigurationAggregation.getCount().multiply(secondsPerSample));

                        virtualMachineConfiguration.setCpu(cpu);
                        virtualMachineConfiguration.setMemory(memory);
                        virtualMachineConfiguration.setDuration(duration);

                        virtualMachine.getVirtualMachineConfigurations().add(virtualMachineConfiguration);

                        Optional<VirtualMachineConfiguration> virtualMachineConfigurationOptional =
                                compute.getTotal()
                                       .stream()
                                       .filter(totalVirtualMachineConfiguration ->
                                               totalVirtualMachineConfiguration.getCpu().equals(virtualMachineConfiguration.getCpu())
                                                       &&
                                                       totalVirtualMachineConfiguration.getMemory().equals(virtualMachineConfiguration.getMemory())
                                       )
                                       .findFirst();

                        if (virtualMachineConfigurationOptional.isPresent()) {
                            final VirtualMachineConfiguration totalVirtualMachineConfiguration =
                                    compute.getTotal().get(compute.getTotal().indexOf(virtualMachineConfigurationOptional.get()));

                            totalVirtualMachineConfiguration.setDuration(totalVirtualMachineConfiguration.getDuration().add(virtualMachineConfiguration.getDuration()));
                        } else {
                            final VirtualMachineConfiguration totalVirtualMachineConfiguration = new VirtualMachineConfiguration();
                            totalVirtualMachineConfiguration.setCpu(virtualMachineConfiguration.getCpu());
                            totalVirtualMachineConfiguration.setMemory(virtualMachineConfiguration.getMemory());
                            totalVirtualMachineConfiguration.setDuration(virtualMachineConfiguration.getDuration());

                            compute.getTotal().add(totalVirtualMachineConfiguration);
                        }
                    });

                    compute.getVirtualMachines().add(virtualMachine);
                }
            });

            domainsMap.put(domainAggregation.getUuid(), domain);
        });
    }
}
