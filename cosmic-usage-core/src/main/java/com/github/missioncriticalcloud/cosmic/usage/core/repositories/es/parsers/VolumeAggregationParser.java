package com.github.missioncriticalcloud.cosmic.usage.core.repositories.es.parsers;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.DOMAINS_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.PAYLOAD_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.RESOURCES_AGGREGATION;
import static com.github.missioncriticalcloud.cosmic.usage.core.utils.MetricsConstants.VOLUME_AGGREGATION;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.VolumeAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.VolumeSizeAggregation;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.RootAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.springframework.stereotype.Component;

@Component
public class VolumeAggregationParser implements AggregationParser {

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

                final VolumeAggregation volumeAggregation = new VolumeAggregation();

                volumeAggregation.setUuid(resourceBucket.getKey());
                volumeAggregation.setCount(BigDecimal.valueOf(resourceBucket.getCount()));

                final RootAggregation payloadAggregation = resourceBucket.getAggregation(PAYLOAD_AGGREGATION, RootAggregation.class);
                final TermsAggregation sizeAggregation = payloadAggregation.getTermsAggregation(VOLUME_AGGREGATION);
                sizeAggregation.getBuckets().forEach(sizeBucket -> {

                    final VolumeSizeAggregation volumeSizeAggregation = new VolumeSizeAggregation();
                    volumeSizeAggregation.setSize(BigDecimal.valueOf(Double.parseDouble(sizeBucket.getKey())));
                    volumeSizeAggregation.setCount(BigDecimal.valueOf(sizeBucket.getCount()));

                    volumeAggregation.getVolumeSizeAggregations().add(volumeSizeAggregation);
                });

                domainAggregation.getVolumeAggregations().add(volumeAggregation);
            });

            domainAggregations.add(domainAggregation);
        });

        return domainAggregations;
    }
}
