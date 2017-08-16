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
import com.github.missioncriticalcloud.cosmic.usage.core.model.VolumeConfiguration;
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
            final BigDecimal secondsPerSample,
            final DataUnit dataUnit,
            final TimeUnit timeUnit,
            final List<DomainAggregation> aggregations,
            final boolean detailed
    ) {
        aggregations.forEach(domainAggregation -> {
            final String domainAggregationUuid = domainAggregation.getUuid();
            final Domain domain = domainsMap.getOrDefault(domainAggregationUuid, new Domain(domainAggregationUuid));

            final Storage storage = domain.getUsage().getStorage();

            domainAggregation.getVolumeAggregations().forEach(volumeAggregation -> {
                final Volume volume = volumesRepository.get(volumeAggregation.getUuid());

                if (volume != null) {
                    volumeAggregation.getVolumeConfigurationAggregations().forEach(volumeConfigurationAggregation -> {
                        final VolumeConfiguration volumeConfiguration = new VolumeConfiguration();

                        final BigDecimal size = dataUnit.convert(volumeConfigurationAggregation.getSize());
                        final BigDecimal duration = timeUnit.convert(volumeConfigurationAggregation.getCount().multiply(secondsPerSample));

                        volumeConfiguration.setSize(size);
                        volumeConfiguration.setDuration(duration);

                        volume.getVolumeConfigurations().add(volumeConfiguration);

                        Optional<VolumeConfiguration> volumeConfigurationOptional = storage.getTotal()
                                                                                           .stream()
                                                                                           .filter(totalVolumeConfiguration ->
                                                                                                   totalVolumeConfiguration.getSize().equals(volumeConfiguration.getSize())
                                                                                           )
                                                                                           .findFirst();

                        if (volumeConfigurationOptional.isPresent()) {
                            final VolumeConfiguration totalVolumeConfiguration = storage.getTotal().get(storage.getTotal().indexOf(volumeConfigurationOptional.get()));

                            totalVolumeConfiguration.setDuration(totalVolumeConfiguration.getDuration().add(volumeConfiguration.getDuration()));
                        } else {
                            final VolumeConfiguration totalVolumeConfiguration = new VolumeConfiguration();
                            totalVolumeConfiguration.setSize(volumeConfiguration.getSize());
                            totalVolumeConfiguration.setDuration(volumeConfiguration.getDuration());

                            storage.getTotal().add(totalVolumeConfiguration);
                        }
                    });

                    storage.getVolumes().add(volume);
                }
            });

            domainsMap.put(domainAggregation.getUuid(), domain);
        });
    }
}
