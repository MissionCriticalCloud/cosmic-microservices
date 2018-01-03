package com.github.missioncriticalcloud.cosmic.usage.core.repositories.es;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DATE_FORMATTER;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.DOMAINS_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.DOMAIN_UUID_FIELD;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.MAX_DOMAIN_AGGREGATIONS;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.MAX_RESOURCE_AGGREGATIONS;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.RESOURCES_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.RESOURCE_TYPE_FIELD;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.RESOURCE_UUID_FIELD;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.TIMESTAMP_FIELD;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.model.types.ResourceType;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.es.parsers.PublicIpAggregationParser;
import io.searchbox.client.JestClient;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository("networkingRepository")
public class NetworkingEsRepository extends MetricsEsRepository {

    private PublicIpAggregationParser publicIpAggregationParser;

    @Autowired
    public NetworkingEsRepository(final JestClient client, final PublicIpAggregationParser publicIpAggregationParser) {
        super(client);
        this.publicIpAggregationParser = publicIpAggregationParser;
    }

    @Override
    public DomainAggregation getDomainAggregation(final String domainUuid, final DateTime from, final DateTime to) {

        final SearchSourceBuilder searchBuilder = new SearchSourceBuilder().size(0);

        final BoolQueryBuilder queryBuilder = boolQuery()
                .must(rangeQuery(TIMESTAMP_FIELD)
                        .gte(DATE_FORMATTER.print(from))
                        .lt(DATE_FORMATTER.print(to))
                )
                .must(termQuery(RESOURCE_TYPE_FIELD, ResourceType.PUBLIC_IP.getValue()));

        if (StringUtils.isEmpty(domainUuid)) {
            throw new IllegalArgumentException();
        }

        queryBuilder.must(termQuery(DOMAIN_UUID_FIELD, domainUuid));
        searchBuilder.query(queryBuilder)
                     .aggregation(terms(DOMAINS_AGGREGATION)
                             .field(DOMAIN_UUID_FIELD)
                             .size(MAX_DOMAIN_AGGREGATIONS)
                             .subAggregation(terms(RESOURCES_AGGREGATION)
                                     .field(RESOURCE_UUID_FIELD)
                                     .size(MAX_RESOURCE_AGGREGATIONS)
                             )
                     );

        final SearchResult result = search(searchBuilder);
        return publicIpAggregationParser.parse(result);
    }

}
