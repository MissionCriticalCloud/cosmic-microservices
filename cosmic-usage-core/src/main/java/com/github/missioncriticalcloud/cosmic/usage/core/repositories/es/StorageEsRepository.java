package com.github.missioncriticalcloud.cosmic.usage.core.repositories.es;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DATE_FORMATTER;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.DOMAINS_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.DOMAIN_UUID_FIELD;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.MAX_DOMAIN_AGGREGATIONS;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.MAX_RESOURCE_AGGREGATIONS;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.PAYLOAD_SIZE_FIELD;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.RESOURCES_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.RESOURCE_TYPE_FIELD;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.RESOURCE_UUID_FIELD;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.TIMESTAMP_FIELD;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.VOLUME_AGGREGATION;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.rangeQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.model.types.ResourceType;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.es.parsers.VolumeAggregationParser;
import io.searchbox.client.JestClient;
import io.searchbox.core.SearchResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository("storageRepository")
public class StorageEsRepository extends MetricsEsRepository {

    private VolumeAggregationParser volumeAggregationParser;

    @Autowired
    public StorageEsRepository(final JestClient client, final VolumeAggregationParser volumeAggregationParser) {
        super(client);
        this.volumeAggregationParser = volumeAggregationParser;
    }

    @Override
    public List<DomainAggregation> list(final String domainUuid, final DateTime from, final DateTime to) {

        final SearchSourceBuilder searchBuilder = new SearchSourceBuilder().size(0);

        final BoolQueryBuilder queryBuilder = boolQuery()
                .must(rangeQuery(TIMESTAMP_FIELD)
                        .gte(DATE_FORMATTER.print(from))
                        .lt(DATE_FORMATTER.print(to))
                )
                .must(termQuery(RESOURCE_TYPE_FIELD, ResourceType.VOLUME.getValue()));

        if (!StringUtils.isEmpty(domainUuid)) {
            queryBuilder.must(termQuery(DOMAIN_UUID_FIELD, domainUuid));
        }

        searchBuilder.query(queryBuilder)
                     .aggregation(terms(DOMAINS_AGGREGATION)
                             .field(DOMAIN_UUID_FIELD)
                             .size(MAX_DOMAIN_AGGREGATIONS)
                             .subAggregation(terms(RESOURCES_AGGREGATION)
                                     .field(RESOURCE_UUID_FIELD)
                                     .size(MAX_RESOURCE_AGGREGATIONS)
                                     .subAggregation(terms(VOLUME_AGGREGATION)
                                             .field(PAYLOAD_SIZE_FIELD)
                                     )
                             )
                     );

        final SearchResult result = search(searchBuilder);
        return volumeAggregationParser.parse(result);
    }
}
