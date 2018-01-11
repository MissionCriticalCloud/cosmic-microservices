package com.github.missioncriticalcloud.cosmic.usage.core.repositories.es.parsers;

import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import io.searchbox.core.SearchResult;

public interface AggregationParser {

    DomainAggregation parse(SearchResult searchResult);
}
