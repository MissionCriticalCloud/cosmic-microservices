package com.github.missioncriticalcloud.cosmic.usage.core.repositories.es.parsers;

import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import io.searchbox.core.SearchResult;

public interface AggregationParser {

    List<DomainAggregation> parse(SearchResult searchResult);
}
