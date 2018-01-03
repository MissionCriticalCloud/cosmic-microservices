package com.github.missioncriticalcloud.cosmic.usage.core.repositories.es.parsers;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.DOMAINS_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.RESOURCES_AGGREGATION;

import java.math.BigDecimal;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.exceptions.UnableToSearchMetricsException;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.PublicIpAggregation;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.springframework.stereotype.Component;

@Component
public class PublicIpAggregationParser implements AggregationParser {

    public DomainAggregation parse(final SearchResult searchResult) {
        if (searchResult.getTotal() == 0) {
            return null;
        }

        final TermsAggregation domainsAggregation = searchResult.getAggregations().getTermsAggregation(DOMAINS_AGGREGATION);
        List<TermsAggregation.Entry> domainBuckets = domainsAggregation.getBuckets();
        if (domainBuckets.size() != 1) {
            throw new UnableToSearchMetricsException();
        }
        TermsAggregation.Entry domainBucket = domainBuckets.get(0);

        final DomainAggregation domainAggregation = new DomainAggregation(domainBucket.getKey());

        final TermsAggregation resourcesAggregation = domainBucket.getTermsAggregation(RESOURCES_AGGREGATION);
        resourcesAggregation.getBuckets().forEach(resourceBucket -> {

            final PublicIpAggregation publicIpAggregation = new PublicIpAggregation();
            publicIpAggregation.setUuid(resourceBucket.getKey());
            publicIpAggregation.setCount(BigDecimal.valueOf(resourceBucket.getCount()));

            domainAggregation.getPublicIpAggregations().add(publicIpAggregation);
        });

        return domainAggregation;
    }

}
