package com.github.missioncriticalcloud.cosmic.api.usage.services.impl;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DEFAULT_ROUNDING_MODE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.missioncriticalcloud.cosmic.api.usage.exceptions.NoMetricsFoundException;
import com.github.missioncriticalcloud.cosmic.api.usage.repositories.DomainsRepository;
import com.github.missioncriticalcloud.cosmic.api.usage.repositories.PublicIpsRepository;
import com.github.missioncriticalcloud.cosmic.api.usage.repositories.ResourcesRepository;
import com.github.missioncriticalcloud.cosmic.api.usage.repositories.VirtualMachinesRepository;
import com.github.missioncriticalcloud.cosmic.api.usage.repositories.VolumesRepository;
import com.github.missioncriticalcloud.cosmic.api.usage.services.UsageService;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Compute;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Network;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Networking;
import com.github.missioncriticalcloud.cosmic.usage.core.model.PublicIp;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Report;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Storage;
import com.github.missioncriticalcloud.cosmic.usage.core.model.VirtualMachine;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Volume;
import com.github.missioncriticalcloud.cosmic.usage.core.model.aggregations.DomainAggregation;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class UsageServiceImpl implements UsageService {

    private final DomainsRepository domainsRepository;
    private final VirtualMachinesRepository virtualMachinesRepository;
    private final VolumesRepository volumesRepository;
    private final PublicIpsRepository publicIpsRepository;

    private ResourcesRepository computeRepository;
    private ResourcesRepository storageRepository;
    private ResourcesRepository networkingRepository;

    private final String scanInterval;

    @Autowired
    public UsageServiceImpl(
            final DomainsRepository domainsRepository,
            final VirtualMachinesRepository virtualMachinesRepository,
            final VolumesRepository volumesRepository,
            final PublicIpsRepository publicIpsRepository,
            @Qualifier("computeRepository") final ResourcesRepository computeRepository,
            @Qualifier("storageRepository") final ResourcesRepository storageRepository,
            @Qualifier("networkingRepository") final ResourcesRepository networkingRepository,
            @Value("${cosmic.usage-api.scan-interval}") final String scanInterval
    ) {
        this.domainsRepository = domainsRepository;
        this.virtualMachinesRepository = virtualMachinesRepository;
        this.volumesRepository = volumesRepository;
        this.publicIpsRepository = publicIpsRepository;

        this.computeRepository = computeRepository;
        this.storageRepository = storageRepository;
        this.networkingRepository = networkingRepository;

        this.scanInterval = scanInterval;
    }

    @Override
    public Report calculate(final DateTime from, final DateTime to, final String path, final boolean detailed) {
        final Map<String, Domain> domainsMap = getDomainsMap(path);
        final Set<String> domainUuids = domainsMap.keySet();

        final List<DomainAggregation> computeDomainAggregations = computeRepository.list(domainUuids, from, to);
        final List<DomainAggregation> storageDomainAggregations = storageRepository.list(domainUuids, from, to);
        final List<DomainAggregation> networkingDomainAggregations = networkingRepository.list(domainUuids, from, to);

        final BigDecimal expectedSampleCount = calculateExpectedSampleCount(from, to);

        mergeComputeDomainAggregations(domainsMap, expectedSampleCount, computeDomainAggregations, detailed);
        mergeStorageDomainAggregations(domainsMap, expectedSampleCount, storageDomainAggregations, detailed);
        mergeNetworkingDomainAggregations(domainsMap, expectedSampleCount, networkingDomainAggregations, detailed);
        removeEmptyDomains(domainsMap);

        if (domainsMap.isEmpty()) {
            throw new NoMetricsFoundException();
        }

        final Report report = new Report();
        report.getDomains().addAll(domainsMap.values());

        return report;
    }

    private Map<String, Domain> getDomainsMap(final String path) {
        final Map<String, Domain> domainsMap = new HashMap<>();

        if (StringUtils.hasText(path)) {
            final List<Domain> domains = domainsRepository.list(path);

            if (CollectionUtils.isEmpty(domains)) {
                throw new NoMetricsFoundException();
            }

            domainsMap.putAll(domains.stream().collect(Collectors.toMap(Domain::getUuid, Function.identity())));
        }

        return domainsMap;
    }

    private BigDecimal calculateExpectedSampleCount(final DateTime from, final DateTime to) {
        final Duration duration = new Duration(from, to);
        final BigDecimal durationInSeconds = BigDecimal.valueOf(duration.getStandardSeconds());

        final CronSequenceGenerator cronSequence = new CronSequenceGenerator(scanInterval);
        final Date nextOccurrence = cronSequence.next(new Date());
        final Date followingOccurrence = cronSequence.next(nextOccurrence);

        final Duration interval = new Duration(nextOccurrence.getTime(), followingOccurrence.getTime());
        final BigDecimal intervalInSeconds = BigDecimal.valueOf(interval.getStandardSeconds());

        return durationInSeconds.divide(intervalInSeconds, RoundingMode.UNNECESSARY);
    }

    private void mergeComputeDomainAggregations(
            final Map<String, Domain> domainsMap,
            final BigDecimal expectedSampleCount,
            final List<DomainAggregation> computeDomainAggregations,
            final boolean detailed
    ) {
        computeDomainAggregations.forEach(domainAggregation -> {
            final String domainAggregationUuid = domainAggregation.getUuid();
            final Domain domain = domainsMap.getOrDefault(domainAggregationUuid, new Domain(domainAggregationUuid));

            final Compute compute = domain.getUsage().getCompute();
            final Compute.Total total = compute.getTotal();

            domainAggregation.getVirtualMachineAggregations().forEach(vmAggregation -> {
                final BigDecimal cpu = vmAggregation.getCpuAverage()
                                                    .multiply(vmAggregation.getSampleCount())
                                                    .divide(expectedSampleCount, DEFAULT_ROUNDING_MODE);

                final BigDecimal memory = vmAggregation.getMemoryAverage()
                                                       .multiply(vmAggregation.getSampleCount())
                                                       .divide(expectedSampleCount, DEFAULT_ROUNDING_MODE);

                if (detailed) {
                    final VirtualMachine virtualMachine = virtualMachinesRepository.get(vmAggregation.getUuid());
                    virtualMachine.setCpu(cpu);
                    virtualMachine.setMemory(memory);
                    compute.getVirtualMachines().add(virtualMachine);
                }

                total.addCpu(cpu);
                total.addMemory(memory);
            });

            domainsMap.put(domainAggregation.getUuid(), domain);
        });
    }

    private void mergeStorageDomainAggregations(
            final Map<String, Domain> domainsMap,
            final BigDecimal expectedSampleCount,
            final List<DomainAggregation> storageDomainAggregations,
            final boolean detailed
    ) {
        storageDomainAggregations.forEach(domainAggregation -> {
            final String domainAggregationUuid = domainAggregation.getUuid();
            final Domain domain = domainsMap.getOrDefault(domainAggregationUuid, new Domain(domainAggregationUuid));

            final Storage storage = domain.getUsage().getStorage();

            domainAggregation.getVolumeAggregations().forEach(volumeAggregation -> {
                BigDecimal size = volumeAggregation.getSize()
                                        .multiply(volumeAggregation.getSampleCount())
                                        .divide(expectedSampleCount, DEFAULT_ROUNDING_MODE);

                if (detailed) {
                    final Volume volume = volumesRepository.get(volumeAggregation.getUuid());
                    volume.setSize(size);
                    storage.getVolumes().add(volume);
                }

                storage.addTotal(size);
            });

            domainsMap.put(domainAggregation.getUuid(), domain);
        });
    }

    private void mergeNetworkingDomainAggregations(
            final Map<String, Domain> domainsMap,
            final BigDecimal expectedSampleCount,
            final List<DomainAggregation> networkDomainAggregations,
            final boolean detailed
    ) {
        networkDomainAggregations.forEach(domainAggregation -> {
            final String domainAggregationUuid = domainAggregation.getUuid();
            final Domain domain = domainsMap.getOrDefault(domainAggregationUuid, new Domain(domainAggregationUuid));

            final Networking networking = domain.getUsage().getNetworking();
            final Networking.Total total = networking.getTotal();

            final Map<String, Network> networksMap = new HashMap<>();
            domainAggregation.getPublicIpAggregations().forEach(publicIpAggregation -> {
                domain.getUsage()
                      .getNetworking()
                      .getTotal()
                      .addPublicIps(publicIpAggregation.getSampleCount()
                                                       .divide(expectedSampleCount, DEFAULT_ROUNDING_MODE)
                      );

                if (detailed) {
                    final PublicIp publicIp = publicIpsRepository.get(publicIpAggregation.getUuid());
                    final Network publicIpNetwork = publicIp.getNetwork();

                    final Network network = networksMap.getOrDefault(publicIpNetwork.getUuid(), publicIpNetwork);
                    network.getPublicIps().add(publicIp);
                    networksMap.put(network.getUuid(), network);
                }

                total.addPublicIps(BigDecimal.ONE);
            });

            networking.getNetworks().addAll(networksMap.values());
            domainsMap.put(domainAggregationUuid, domain);
        });
    }

    private void removeEmptyDomains(final Map<String, Domain> domainsMap) {
        final Set<String> uuidsToRemove = new HashSet<>();
        domainsMap.forEach((uuid, domain) -> {
            if (domain.getUsage().isEmpty()) {
                uuidsToRemove.add(uuid);
            }
        });
        uuidsToRemove.forEach(domainsMap::remove);
    }

}
