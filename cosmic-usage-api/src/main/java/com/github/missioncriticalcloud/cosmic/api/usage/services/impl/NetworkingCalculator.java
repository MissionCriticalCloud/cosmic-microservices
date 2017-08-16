package com.github.missioncriticalcloud.cosmic.api.usage.services.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.missioncriticalcloud.cosmic.api.usage.services.AggregationCalculator;
import com.github.missioncriticalcloud.cosmic.usage.core.model.DataUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Network;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Networking;
import com.github.missioncriticalcloud.cosmic.usage.core.model.PublicIp;
import com.github.missioncriticalcloud.cosmic.usage.core.model.TimeUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.PublicIpsRepository;
import org.springframework.stereotype.Service;

@Service("networkingCalculator")
public class NetworkingCalculator implements AggregationCalculator<DomainAggregation> {

    private final PublicIpsRepository publicIpsRepository;

    public NetworkingCalculator(final PublicIpsRepository publicIpsRepository) {
        this.publicIpsRepository = publicIpsRepository;
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

            final Networking networking = domain.getUsage().getNetworking();
            final Networking.Total total = networking.getTotal();

            final Map<String, Network> networksMap = new HashMap<>();
            domainAggregation.getPublicIpAggregations().forEach(publicIpAggregation -> {
                final BigDecimal duration = timeUnit.convert(publicIpAggregation.getCount().multiply(secondsPerSample));

                if (detailed) {
                    final PublicIp publicIp = publicIpsRepository.get(publicIpAggregation.getUuid());
                    if (publicIp != null) {
                        publicIp.setDuration(duration);

                        final Network publicIpNetwork = publicIp.getNetwork();
                        final Network network = networksMap.getOrDefault(publicIpNetwork.getUuid(), publicIpNetwork);
                        network.getPublicIps().add(publicIp);
                        networksMap.put(network.getUuid(), network);
                    }
                }

                total.addPublicIps(duration);
            });

            networking.getNetworks().addAll(networksMap.values());
            domainsMap.put(domainAggregationUuid, domain);
        });
    }
}
