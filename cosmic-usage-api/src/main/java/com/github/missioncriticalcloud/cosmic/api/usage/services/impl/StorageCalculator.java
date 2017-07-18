package com.github.missioncriticalcloud.cosmic.api.usage.services.impl;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DEFAULT_ROUNDING_MODE;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.github.missioncriticalcloud.cosmic.api.usage.services.AggregationCalculator;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Storage;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Unit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Volume;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.VolumesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("storageCalculator")
public class StorageCalculator implements AggregationCalculator<DomainAggregation> {

    private final VolumesRepository volumesRepository;

    @Autowired
    public StorageCalculator(final VolumesRepository volumesRepository) {
        this.volumesRepository = volumesRepository;
    }

    @Override
    public void calculateAndMerge(
            final Map<String, Domain> domainsMap,
            final BigDecimal expectedSampleCount,
            final Unit unit,
            final List<DomainAggregation> aggregations,
            final boolean detailed
    ) {
        aggregations.forEach(domainAggregation -> {
            final String domainAggregationUuid = domainAggregation.getUuid();
            final Domain domain = domainsMap.getOrDefault(domainAggregationUuid, new Domain(domainAggregationUuid));

            final Storage storage = domain.getUsage().getStorage();

            domainAggregation.getVolumeAggregations().forEach(volumeAggregation -> {
                final BigDecimal size = unit.convert(
                        volumeAggregation.getSize()
                                         .multiply(volumeAggregation.getSampleCount())
                                         .divide(expectedSampleCount, DEFAULT_ROUNDING_MODE)
                );

                if (detailed) {
                    final Volume volume = volumesRepository.get(volumeAggregation.getUuid());
                    if (volume != null) {
                        volume.setSize(size);
                        storage.getVolumes().add(volume);
                    }
                }

                storage.addTotal(size);
            });

            domainsMap.put(domainAggregation.getUuid(), domain);
        });
    }
}
