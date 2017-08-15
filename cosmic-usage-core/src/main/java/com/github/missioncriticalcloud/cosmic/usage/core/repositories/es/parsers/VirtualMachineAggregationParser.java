package com.github.missioncriticalcloud.cosmic.usage.core.repositories.es.parsers;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.CPU_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.DOMAINS_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.MEMORY_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.PAYLOAD_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.RESOURCES_AGGREGATION;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.VirtualMachineAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.VirtualMachineConfigurationAggregation;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.RootAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.springframework.stereotype.Component;

@Component
public class VirtualMachineAggregationParser implements AggregationParser {

    public List<DomainAggregation> parse(final SearchResult searchResult) {
        final List<DomainAggregation> domainAggregations = new LinkedList<>();

        if (searchResult.getTotal() == 0) {
            return domainAggregations;
        }

        final TermsAggregation domainsAggregation = searchResult.getAggregations().getTermsAggregation(DOMAINS_AGGREGATION);
        domainsAggregation.getBuckets().forEach(domainBucket -> {

            final DomainAggregation domainAggregation = new DomainAggregation(domainBucket.getKey());

            final TermsAggregation resourcesAggregation = domainBucket.getTermsAggregation(RESOURCES_AGGREGATION);
            resourcesAggregation.getBuckets().forEach(resourceBucket -> {

                final VirtualMachineAggregation virtualMachineAggregation = new VirtualMachineAggregation();
                virtualMachineAggregation.setUuid(resourceBucket.getKey());
                virtualMachineAggregation.setCount(BigDecimal.valueOf(resourceBucket.getCount()));

                final RootAggregation payloadAggregation = resourceBucket.getAggregation(PAYLOAD_AGGREGATION, RootAggregation.class);
                final TermsAggregation cpuAggregation = payloadAggregation.getTermsAggregation(CPU_AGGREGATION);
                cpuAggregation.getBuckets().forEach(cpuBucket -> {

                    TermsAggregation memoryAggregation = cpuBucket.getTermsAggregation(MEMORY_AGGREGATION);
                    memoryAggregation.getBuckets().forEach(memoryBucket -> {

                        final VirtualMachineConfigurationAggregation virtualMachineConfigurationAggregation = new VirtualMachineConfigurationAggregation();
                        virtualMachineConfigurationAggregation.setCpu(BigDecimal.valueOf(Double.parseDouble(cpuBucket.getKey())));
                        virtualMachineConfigurationAggregation.setMemory(BigDecimal.valueOf(Double.parseDouble(memoryBucket.getKey())));
                        virtualMachineConfigurationAggregation.setCount(BigDecimal.valueOf(memoryBucket.getCount()));

                        virtualMachineAggregation.getVirtualMachineConfigurationAggregations().add(virtualMachineConfigurationAggregation);
                    });
                });

                domainAggregation.getVirtualMachineAggregations().add(virtualMachineAggregation);
            });

            domainAggregations.add(domainAggregation);
        });

        return domainAggregations;
    }
}
