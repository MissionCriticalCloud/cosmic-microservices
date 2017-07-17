package com.github.missioncriticalcloud.cosmic.billingreporter.repositories;

import java.util.LinkedList;
import java.util.List;

import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.springframework.stereotype.Component;

@Component
public class DomainsAggregationParser {

    static final String DOMAINS_AGGREGATION = "domains";

    public List<String> parse(final SearchResult searchResult) {

        final List<String> domains = new LinkedList<>();

        final TermsAggregation domainsAggregation = searchResult.getAggregations().getTermsAggregation(DOMAINS_AGGREGATION);

        domainsAggregation.getBuckets().forEach(domainsBucket -> domains.add(domainsBucket.getKeyAsString()));

        return domains;
    }
}
