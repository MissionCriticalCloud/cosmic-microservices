package com.github.missioncriticalcloud.cosmic.usage.core.repositories.es.parsers;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.CPU_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.DOMAINS_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.MEMORY_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.RESOURCES_AGGREGATION;

import java.math.BigDecimal;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.exceptions.UnableToSearchMetricsException;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.InstanceTypeAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.VirtualMachineAggregation;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.springframework.stereotype.Component;

@Component
public class VirtualMachineAggregationParser implements AggregationParser {

    public DomainAggregation parse(final SearchResult searchResult) {
        if (searchResult.getTotal() == 0) {
            return null;
        }

        final TermsAggregation domainsAggregation = searchResult.getAggregations().getTermsAggregation(DOMAINS_AGGREGATION);
        List<TermsAggregation.Entry> domainBuckets = domainsAggregation.getBuckets();
        if (domainBuckets.size() != 1) {
            throw new UnableToSearchMetricsException();
        }
        final TermsAggregation.Entry domainBucket = domainBuckets.get(0);

            final DomainAggregation domainAggregation = new DomainAggregation(domainBucket.getKey());

            final TermsAggregation resourcesAggregation = domainBucket.getTermsAggregation(RESOURCES_AGGREGATION);
            resourcesAggregation.getBuckets().forEach(resourceBucket -> {

                final VirtualMachineAggregation virtualMachineAggregation = new VirtualMachineAggregation();
                virtualMachineAggregation.setUuid(resourceBucket.getKey());
                virtualMachineAggregation.setCount(BigDecimal.valueOf(resourceBucket.getCount()));

                    final TermsAggregation cpuAggregation = resourceBucket.getTermsAggregation(CPU_AGGREGATION);
                    cpuAggregation.getBuckets().forEach(cpuBucket -> {

                        TermsAggregation memoryAggregation = cpuBucket.getTermsAggregation(MEMORY_AGGREGATION);
                        memoryAggregation.getBuckets().forEach(memoryBucket -> {

                            final InstanceTypeAggregation instanceTypeAggregation = new InstanceTypeAggregation();
                            instanceTypeAggregation.setCpu(BigDecimal.valueOf(Double.parseDouble(cpuBucket.getKey())));
                            instanceTypeAggregation.setMemory(BigDecimal.valueOf(Double.parseDouble(memoryBucket.getKey())));
                            instanceTypeAggregation.setCount(BigDecimal.valueOf(memoryBucket.getCount()));

                            virtualMachineAggregation.getInstanceTypeAggregations().add(instanceTypeAggregation);
                        });
                    });

                domainAggregation.getVirtualMachineAggregations().add(virtualMachineAggregation);
            });

            return domainAggregation;
    }

}
