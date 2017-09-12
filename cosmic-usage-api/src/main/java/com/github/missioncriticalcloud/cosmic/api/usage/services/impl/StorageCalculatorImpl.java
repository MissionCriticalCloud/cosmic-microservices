package com.github.missioncriticalcloud.cosmic.api.usage.services.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            final Map<String, Domain> domainsMap,
            final BigDecimal secondsPerSample,
            final DataUnit dataUnit,
            final TimeUnit timeUnit,
            final List<DomainAggregation> aggregations
    ) {
        aggregations.forEach(domainAggregation -> {
            final String domainAggregationUuid = domainAggregation.getUuid();
            final Domain domain = domainsMap.getOrDefault(domainAggregationUuid, new Domain(domainAggregationUuid));

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

                    final Optional<VolumeSize> volumeSizeOptional = storage.getVolumeSizes()
                                                                           .stream()
                                                                           .filter(totalVolumeSize ->
                                                                                   totalVolumeSize.getSize().equals(volumeSize.getSize())
                                                                           )
                                                                           .findFirst();

                    if (volumeSizeOptional.isPresent()) {
                        final VolumeSize totalVolumeSize = storage.getVolumeSizes().get(storage.getVolumeSizes().indexOf(volumeSizeOptional.get()));
                        totalVolumeSize.setDuration(totalVolumeSize.getDuration().add(volumeSize.getDuration()));
                    } else {
                        final VolumeSize totalVolumeSize = new VolumeSize();
                        totalVolumeSize.setSize(volumeSize.getSize());
                        totalVolumeSize.setDuration(volumeSize.getDuration());

                        storage.getVolumeSizes().add(totalVolumeSize);
                    }
                });

                storage.getVolumes().add(volume);
            });

            domainsMap.put(domainAggregation.getUuid(), domain);
        });
    }
}
