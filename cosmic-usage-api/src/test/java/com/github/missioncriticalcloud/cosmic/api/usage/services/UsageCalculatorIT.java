package com.github.missioncriticalcloud.cosmic.api.usage.services;

import static com.github.missioncriticalcloud.cosmic.usage.core.utils.FormatUtils.DATE_FORMATTER;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.usage.core.exceptions.NoMetricsFoundException;
import com.github.missioncriticalcloud.cosmic.usage.core.model.DataUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.InstanceType;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Network;
import com.github.missioncriticalcloud.cosmic.usage.core.model.PublicIp;
import com.github.missioncriticalcloud.cosmic.usage.core.model.TimeUnit;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Usage;
import com.github.missioncriticalcloud.cosmic.usage.core.model.VirtualMachine;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Volume;
import com.github.missioncriticalcloud.cosmic.usage.core.model.VolumeSize;
import com.github.missioncriticalcloud.cosmic.usage.core.model.types.NetworkType;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc.DomainsJdbcRepository;
import com.github.missioncriticalcloud.cosmic.usage.testresources.EsTestUtils;
import io.searchbox.client.JestClient;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
@Sql(value = {"/test-schema.sql", "/domains-repository-test-data.sql"})
public class UsageCalculatorIT {

    @Autowired
    private JestClient jestClient;

    @Autowired
    private UsageCalculator usageCalculator;

    @Autowired
    private DomainsJdbcRepository domainsRepository;

    @After
    public void cleanup() throws IOException {
        EsTestUtils.destroyData(jestClient);
    }

    @Test(expected = NoMetricsFoundException.class)
    public void testIfNoMetricsAreFoundWhenDataIsNotLoaded() throws IOException {
        EsTestUtils.setupIndex(jestClient);

        final DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2017-01-02");
        Domain domain = domainsRepository.get("domain_uuid1");

        usageCalculator.calculateDetailed(from, to, domain, DataUnit.BYTES, TimeUnit.SECONDS);
    }

    @Test(expected = NoMetricsFoundException.class)
    public void testIfNoMetricsAreFoundWhenInternalDoesNotMatch() throws IOException {
        EsTestUtils.setupIndex(jestClient);
        EsTestUtils.setupData(jestClient, "/cosmic-metrics-es-data.json");

        final DateTime from = DATE_FORMATTER.parseDateTime("2000-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2000-01-01");

        usageCalculator.calculateDetailed(from, to, new Domain("domain_missing"), DataUnit.BYTES, TimeUnit.SECONDS);
    }

    @Test
    public void testRootPathDomain() throws IOException {
        EsTestUtils.setupIndex(jestClient);
        EsTestUtils.setupData(jestClient, "/cosmic-metrics-es-data.json");

        final DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2017-01-02");
        final Domain domain = domainsRepository.get("domain_uuid1");

        usageCalculator.calculateDetailed(from, to, domain, DataUnit.BYTES, TimeUnit.SECONDS);
        assertThat(domain).isNotNull();
        assertDomain1(domain);
    }

    @Test
    public void testLevel1PathDomain() throws IOException {
        EsTestUtils.setupIndex(jestClient);
        EsTestUtils.setupData(jestClient, "/cosmic-metrics-es-data.json");

        final DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2017-01-02");
        final Domain domain = domainsRepository.get("domain_uuid2");

        usageCalculator.calculateDetailed(from, to, domain, DataUnit.BYTES, TimeUnit.SECONDS);
        assertThat(domain).isNotNull();
        assertDomain2(domain);
    }

    @Test(expected = NoMetricsFoundException.class)
    public void testLevel2PathDomain() throws Exception {
        EsTestUtils.setupIndex(jestClient);
        EsTestUtils.setupData(jestClient, "/cosmic-metrics-es-data.json");

        final DateTime from = DATE_FORMATTER.parseDateTime("2017-01-01");
        final DateTime to = DATE_FORMATTER.parseDateTime("2017-01-02");
        final Domain level2Domain = domainsRepository.get("domain_uuid3");

        usageCalculator.calculateDetailed(from, to, level2Domain, DataUnit.BYTES, TimeUnit.SECONDS);
    }

    private void assertDomain1(final Domain domain) {
            assertThat(domain.getUuid()).isEqualTo("domain_uuid1");
            assertThat(domain.getName()).isNotNull();
            assertThat(domain.getName()).isEqualTo("ROOT");
            assertThat(domain.getPath()).isEqualTo("/");

            final Usage usage = domain.getUsage();
            assertThat(usage).isNotNull();

            // test value per vm
            List<VirtualMachine> virtualMachines = usage.getCompute().getVirtualMachines();
            virtualMachines.stream().filter(virtualMachine -> "vm_instance_uuid1".equals(virtualMachine.getUuid())).forEach(virtualMachine -> {
                List<InstanceType> instanceTypes = virtualMachine.getInstanceTypes();
                assertInstanceType(instanceTypes.get(0), 4, 400, 1800);
                assertInstanceType(instanceTypes.get(1), 2, 400, 900);
            });

            virtualMachines.stream().filter(virtualMachine -> "vm_instance_uuid3".equals(virtualMachine.getUuid())).forEach(virtualMachine -> {
                List<InstanceType> instanceTypes = virtualMachine.getInstanceTypes();
                assertInstanceType(instanceTypes.get(0), 4, 400, 900);
            });
        
            // test values per volume:
            List<Volume> volumes = usage.getStorage().getVolumes();
            volumes.stream().filter(volume -> "storage_uuid1".equals(volume.getUuid())).forEach(volume ->
                    assertVolumeSize(volume.getVolumeSizes().get(0), 144000, 900)
            );

            // test ip address usage
            List<Network> networks = usage.getNetworking().getNetworks();
            assertThat(networks).hasSize(1);
            networks.stream().filter(network -> NetworkType.GUEST.equals(network.getType())).forEach(
                    network -> assertPublicIpAddress(network, 900, 1)
            );
    }

    private void assertDomain2(final Domain domain) {
            assertThat(domain.getUuid()).isEqualTo("domain_uuid2");
            assertThat(domain.getName()).isNotNull();
            assertThat(domain.getName()).isEqualTo("level1");
            assertThat(domain.getPath()).isEqualTo("/level1");

            final Usage usage = domain.getUsage();
            assertThat(usage).isNotNull();

            // test values per vm
            List<VirtualMachine> virtualMachines = usage.getCompute().getVirtualMachines();
            virtualMachines.stream().filter(virtualMachine -> "vm_instance_uuid2".equals(virtualMachine.getUuid())).forEach(virtualMachine -> {
                List<InstanceType> vmInstanceTypes = virtualMachine.getInstanceTypes();
                assertInstanceType(vmInstanceTypes.get(0), 4, 800, 900);
            });

            // test values per volume
            List<Volume> volumes = usage.getStorage().getVolumes();
            volumes.stream().filter(volume -> "storage_uuid3".equals(volume.getUuid())).forEach(volume -> {

                assertVolumeSize(volume.getVolumeSizes().get(0), 57600, 900);
                assertVolumeSize(volume.getVolumeSizes().get(1), 288000, 900);
            });

            volumes.stream().filter(volume -> "storage_uuid2".equals(volume.getUuid())).forEach(volume -> {
                assertVolumeSize(volume.getVolumeSizes().get(0), 288000, 900);
            });

            // test ip address usage
            List<Network> networks = usage.getNetworking().getNetworks();
            assertThat(networks).hasSize(1);
            networks.stream().filter(network -> NetworkType.GUEST.equals(network.getType())).forEach(
                    network -> assertPublicIpAddress(network, 900, 2)
            );
    }

    private void assertInstanceType(final InstanceType instanceType, final double expectedCpu, final double expectedMemory, final double expectedDuration) {
        assertThat(instanceType).isNotNull();

        final BigDecimal cpu = instanceType.getCpu();
        assertThat(cpu).isNotNull();
        assertThat(cpu).isEqualByComparingTo(BigDecimal.valueOf(expectedCpu));

        final BigDecimal memory = instanceType.getMemory();
        assertThat(memory).isNotNull();
        assertThat(memory).isEqualByComparingTo(BigDecimal.valueOf(expectedMemory));

        final BigDecimal duration = instanceType.getDuration();
        assertThat(duration).isNotNull();
        assertThat(duration).isEqualByComparingTo(BigDecimal.valueOf(expectedDuration));
    }

    private void assertVolumeSize(final VolumeSize volumeSize, final double expectedSize, final double expectedDuration) {
        assertThat(volumeSize).isNotNull();

        final BigDecimal size = volumeSize.getSize();
        assertThat(size).isNotNull();
        assertThat(size).isEqualByComparingTo(BigDecimal.valueOf(expectedSize));

        final BigDecimal duration = volumeSize.getDuration();
        assertThat(duration).isNotNull();
        assertThat(duration).isEqualByComparingTo(BigDecimal.valueOf(expectedDuration));
    }

    private void assertPublicIpAddress(final Network network, final double expectedDuration, final int expectedSize) {
        List<PublicIp> publicIps = network.getPublicIps();
        assertThat(publicIps).hasSize(expectedSize);
        publicIps.forEach( publicIp -> assertThat(publicIp.getDuration()).isEqualByComparingTo(BigDecimal.valueOf(expectedDuration)));
    }

}
