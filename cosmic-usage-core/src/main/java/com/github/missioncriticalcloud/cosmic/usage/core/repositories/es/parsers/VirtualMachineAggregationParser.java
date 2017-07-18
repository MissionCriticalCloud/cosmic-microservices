package com.github.missioncriticalcloud.cosmic.usage.core.repositories.es.parsers;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.CPU_AVERAGE_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.DOMAINS_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.MEMORY_AVERAGE_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.RESOURCES_AGGREGATION;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.VirtualMachineAggregation;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.AvgAggregation;
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
                virtualMachineAggregation.setSampleCount(BigDecimal.valueOf(resourceBucket.getCount()));

                final AvgAggregation cpuAverage = resourceBucket.getAvgAggregation(CPU_AVERAGE_AGGREGATION);
                final AvgAggregation memoryAverage = resourceBucket.getAvgAggregation(MEMORY_AVERAGE_AGGREGATION);

                virtualMachineAggregation.setCpuAverage(BigDecimal.valueOf(cpuAverage.getAvg()));
                virtualMachineAggregation.setMemoryAverage(BigDecimal.valueOf(memoryAverage.getAvg()));

                domainAggregation.getVirtualMachineAggregations().add(virtualMachineAggregation);
            });

            domainAggregations.add(domainAggregation);
        });

        return domainAggregations;
    }
}
