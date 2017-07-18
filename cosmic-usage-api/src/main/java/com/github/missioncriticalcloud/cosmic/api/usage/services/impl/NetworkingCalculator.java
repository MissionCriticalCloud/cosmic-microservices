package com.github.missioncriticalcloud.cosmic.api.usage.services.impl;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DEFAULT_ROUNDING_MODE;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.missioncriticalcloud.cosmic.api.usage.services.AggregationCalculator;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Network;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Networking;
import com.github.missioncriticalcloud.cosmic.usage.core.model.PublicIp;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Unit;
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
            final BigDecimal expectedSampleCount,
            final Unit unit,
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
                final BigDecimal amount = publicIpAggregation.getSampleCount()
                                                             .divide(expectedSampleCount, DEFAULT_ROUNDING_MODE);

                if (detailed) {
                    final PublicIp publicIp = publicIpsRepository.get(publicIpAggregation.getUuid());
                    if (publicIp != null) {
                        publicIp.setAmount(amount);

                        final Network publicIpNetwork = publicIp.getNetwork();
                        final Network network = networksMap.getOrDefault(publicIpNetwork.getUuid(), publicIpNetwork);
                        network.getPublicIps().add(publicIp);
                        networksMap.put(network.getUuid(), network);
                    }
                }

                total.addPublicIps(amount);
            });

            networking.getNetworks().addAll(networksMap.values());
            domainsMap.put(domainAggregationUuid, domain);
        });
    }
}
