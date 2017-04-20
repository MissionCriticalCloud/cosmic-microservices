package com.github.missioncriticalcloud.cosmic.api.usage.repositories.es;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DATE_FORMATTER;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.index.query.QueryBuilders.termsQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.avg;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

import java.util.List;
import java.util.Set;

import com.github.missioncriticalcloud.cosmic.api.usage.repositories.es.parsers.VolumeAggregationParser;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.model.types.ResourceType;
import io.searchbox.client.JestClient;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("storageRepository")
public class StorageEsRepository extends MetricsEsRepository {

    private VolumeAggregationParser volumeAggregationParser;

    @Autowired
    public StorageEsRepository(final JestClient client, final VolumeAggregationParser volumeAggregationParser) {
        super(client);
        this.volumeAggregationParser = volumeAggregationParser;
    }

    @Override
    public List<DomainAggregation> list(final Set<String> domainUuids, final DateTime from, final DateTime to) {

        final SearchSourceBuilder searchBuilder = new SearchSourceBuilder().size(0);

        final BoolQueryBuilder queryBuilder = boolQuery()
                .must(rangeQuery(TIMESTAMP_FIELD)
                        .gte(DATE_FORMATTER.print(from))
                        .lt(DATE_FORMATTER.print(to))
                )
                .must(termQuery(RESOURCE_TYPE_FIELD, ResourceType.VOLUME.getValue()));

        if (!domainUuids.isEmpty()) {
            queryBuilder.must(termsQuery(DOMAIN_UUID_FIELD, domainUuids));
        }

        searchBuilder.query(queryBuilder)
                     .aggregation(terms(DOMAINS_AGGREGATION)
                             .field(DOMAIN_UUID_FIELD)
                             .size(MAX_DOMAIN_AGGREGATIONS)
                             .subAggregation(terms(RESOURCES_AGGREGATION)
                                     .field(RESOURCE_UUID_FIELD)
                                     .size(MAX_RESOURCE_AGGREGATIONS)
                                     .subAggregation(avg(VOLUME_AVERAGE_AGGREGATION)
                                             .field(PAYLOAD_SIZE_FIELD)
                                     )
                             )
                     );

        final SearchResult result = search(searchBuilder);
        return volumeAggregationParser.parse(result);
    }

}
