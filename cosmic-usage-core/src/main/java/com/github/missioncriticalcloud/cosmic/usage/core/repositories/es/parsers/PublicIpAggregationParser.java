package com.github.missioncriticalcloud.cosmic.usage.core.repositories.es.parsers;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.DOMAINS_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.RESOURCES_AGGREGATION;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.PublicIpAggregation;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.springframework.stereotype.Component;

@Component
public class PublicIpAggregationParser implements AggregationParser {

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

                final PublicIpAggregation publicIpAggregation = new PublicIpAggregation();
                publicIpAggregation.setUuid(resourceBucket.getKey());
                publicIpAggregation.setCount(BigDecimal.valueOf(resourceBucket.getCount()));

                domainAggregation.getPublicIpAggregations().add(publicIpAggregation);
            });

            domainAggregations.add(domainAggregation);
        });

        return domainAggregations;
    }
}
