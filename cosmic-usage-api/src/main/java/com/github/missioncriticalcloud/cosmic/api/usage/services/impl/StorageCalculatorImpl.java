package com.github.missioncriticalcloud.cosmic.api.usage.services.impl;

import java.math.BigDecimal;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.api.usage.services.AggregationCalculator;
import com.github.missioncriticalcloud.cosmic.usage.core.model.DataUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Storage;
import com.github.missioncriticalcloud.cosmic.usage.core.model.TimeUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Volume;
import com.github.missioncriticalcloud.cosmic.usage.core.model.VolumeSize;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.VolumesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("storageCalculator")
public class StorageCalculatorImpl implements AggregationCalculator<DomainAggregation> {

    private final VolumesRepository volumesRepository;

    @Autowired
    public StorageCalculatorImpl(final VolumesRepository volumesRepository) {
        this.volumesRepository = volumesRepository;
    }

    @Override
    public void calculateAndMerge(
            final Domain domain,
            final BigDecimal secondsPerSample,
            final DataUnit dataUnit,
            final TimeUnit timeUnit,
            final List<DomainAggregation> aggregations
    ) {
        aggregations.forEach(domainAggregation -> {
            final Storage storage = domain.getUsage().getStorage();

            domainAggregation.getVolumeAggregations().forEach(volumeAggregation -> {
                final Volume volume = volumesRepository.get(volumeAggregation.getUuid());
                if (volume == null) {
                    return;
                }

                volumeAggregation.getVolumeSizeAggregations().forEach(volumeTypeAggregation -> {

                    final VolumeSize volumeSize = new VolumeSize();

                    final BigDecimal size = dataUnit.convert(volumeTypeAggregation.getSize());
                    volumeSize.setSize(size);

                    final BigDecimal duration = timeUnit.convert(volumeTypeAggregation.getCount().multiply(secondsPerSample));
                    volumeSize.setDuration(duration);

                    volume.getVolumeSizes().add(volumeSize);
                });

                storage.getVolumes().add(volume);
            });
        });
    }

}
