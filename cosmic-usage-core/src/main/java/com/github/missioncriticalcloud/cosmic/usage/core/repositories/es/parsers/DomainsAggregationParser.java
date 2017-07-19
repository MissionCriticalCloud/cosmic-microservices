package com.github.missioncriticalcloud.cosmic.usage.core.repositories.es.parsers;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.DOMAINS_AGGREGATION;

import java.util.LinkedList;
import java.util.List;

import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.springframework.stereotype.Component;

@Component
public class DomainsAggregationParser {

    public List<String> parse(final SearchResult searchResult) {

        final List<String> domains = new LinkedList<>();

        if (searchResult.getTotal() == 0) {
            return domains;
        }

        final TermsAggregation domainsAggregation = searchResult.getAggregations().getTermsAggregation(DOMAINS_AGGREGATION);

        domainsAggregation.getBuckets().forEach(domainsBucket -> domains.add(domainsBucket.getKeyAsString()));

        return domains;
    }
}
