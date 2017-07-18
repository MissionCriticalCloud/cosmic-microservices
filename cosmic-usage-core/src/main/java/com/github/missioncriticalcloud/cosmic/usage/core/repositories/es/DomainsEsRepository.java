package com.github.missioncriticalcloud.cosmic.usage.core.repositories.es;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DATE_FORMATTER;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.DOMAINS_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.DOMAIN_UUID_FIELD;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.MAX_DOMAIN_AGGREGATIONS;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.TIMESTAMP_FIELD;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;

import java.io.IOException;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.exceptions.UnableToSearchMetricsException;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.es.parsers.DomainsAggregationParser;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

@Repository
public class DomainsEsRepository implements com.github.missioncriticalcloud.cosmic.usage.core.repositories.DomainsEsRepository {
    private final JestClient client;

    private DomainsAggregationParser domainsAggregationParser;

    public DomainsEsRepository(final JestClient client, final DomainsAggregationParser domainsAggregationParser) {
        this.client = client;
        this.domainsAggregationParser = domainsAggregationParser;
    }

    private SearchResult search(final SearchSourceBuilder searchBuilder) {
        try {
            return client.execute(
                    new Search.Builder(searchBuilder.toString())
                            .addIndex("cosmic-metrics-*")
                            .addType("metric")
                            .build()
            );
        } catch (IOException e) {
            throw new UnableToSearchMetricsException(e.getMessage(), e);
        }
    }

    @Override
    public List<String> listDomains(final DateTime from, final DateTime to) {
        final SearchSourceBuilder searchBuilder = new SearchSourceBuilder().size(0);

        final BoolQueryBuilder queryBuilder = boolQuery()
                .must(rangeQuery(TIMESTAMP_FIELD)
                        .gte(DATE_FORMATTER.print(from))
                        .lt(DATE_FORMATTER.print(to))
                );

        searchBuilder.query(queryBuilder).aggregation(
                AggregationBuilders.terms(DOMAINS_AGGREGATION)

                                   .field(DOMAIN_UUID_FIELD).size(MAX_DOMAIN_AGGREGATIONS)
        );

        SearchResult searchResult = search(searchBuilder);

        return domainsAggregationParser.parse(searchResult);
    }
}
