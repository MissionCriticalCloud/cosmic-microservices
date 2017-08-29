package com.github.missioncriticalcloud.cosmic.api.usage.utils;

import java.math.BigDecimal;

import com.github.missioncriticalcloud.cosmic.api.usage.utils.SortingUtils.SortBy;
import com.github.missioncriticalcloud.cosmic.api.usage.utils.SortingUtils.SortOrder;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import com.github.missioncriticalcloud.cosmic.usage.core.model.InstanceType;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Network;
import com.github.missioncriticalcloud.cosmic.usage.core.model.PublicIp;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Report;
import com.github.missioncriticalcloud.cosmic.usage.core.model.VolumeSize;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class SortingUtilsTest {

    @Test
    public void sortByDomainPath() {
        final Report report = new Report();

        Domain domain1 = new Domain("1");
        domain1.setPath("/bbb");
        report.getDomains().add(domain1);

        Domain domain2 = new Domain("2");
        domain2.setPath("/ccc");
        report.getDomains().add(domain2);

        Domain domain3 = new Domain("3");
        domain3.setPath("/aaa");
        report.getDomains().add(domain3);

        // Ascending
        SortingUtils.sort(report, SortBy.DOMAIN_PATH, SortOrder.ASC);
        Assertions.assertThat(report.getDomains().get(0)).isEqualTo(domain3);
        Assertions.assertThat(report.getDomains().get(1)).isEqualTo(domain1);
        Assertions.assertThat(report.getDomains().get(2)).isEqualTo(domain2);

        // Descending
        SortingUtils.sort(report, SortBy.DOMAIN_PATH, SortOrder.DESC);
        Assertions.assertThat(report.getDomains().get(0)).isEqualTo(domain2);
        Assertions.assertThat(report.getDomains().get(1)).isEqualTo(domain1);
        Assertions.assertThat(report.getDomains().get(2)).isEqualTo(domain3);
    }

    @Test
    public void sortByCpu() {
        final Report report = new Report();

        Domain domain1 = new Domain("1");
        InstanceType instanceType1 = new InstanceType();
        instanceType1.setCpu(BigDecimal.ONE);
        instanceType1.setDuration(BigDecimal.ONE);
        domain1.getUsage().getCompute().getInstanceTypes().add(instanceType1);
        report.getDomains().add(domain1);

        Domain domain2 = new Domain("2");
        InstanceType instanceType2 = new InstanceType();
        instanceType2.setCpu(BigDecimal.TEN);
        instanceType2.setDuration(BigDecimal.TEN);
        domain2.getUsage().getCompute().getInstanceTypes().add(instanceType2);
        report.getDomains().add(domain2);

        Domain domain3 = new Domain("3");
        InstanceType instanceType3 = new InstanceType();
        instanceType3.setCpu(BigDecimal.ZERO);
        instanceType3.setDuration(BigDecimal.ZERO);
        domain3.getUsage().getCompute().getInstanceTypes().add(instanceType3);
        report.getDomains().add(domain3);

        // Ascending
        SortingUtils.sort(report, SortBy.CPU, SortOrder.ASC);
        Assertions.assertThat(report.getDomains().get(0)).isEqualTo(domain3);
        Assertions.assertThat(report.getDomains().get(1)).isEqualTo(domain1);
        Assertions.assertThat(report.getDomains().get(2)).isEqualTo(domain2);

        // Descending
        SortingUtils.sort(report, SortBy.CPU, SortOrder.DESC);
        Assertions.assertThat(report.getDomains().get(0)).isEqualTo(domain2);
        Assertions.assertThat(report.getDomains().get(1)).isEqualTo(domain1);
        Assertions.assertThat(report.getDomains().get(2)).isEqualTo(domain3);
    }

    @Test
    public void sortByMemory() {
        final Report report = new Report();

        Domain domain1 = new Domain("1");
        InstanceType instanceType1 = new InstanceType();
        instanceType1.setMemory(BigDecimal.ONE);
        instanceType1.setDuration(BigDecimal.ONE);
        domain1.getUsage().getCompute().getInstanceTypes().add(instanceType1);
        report.getDomains().add(domain1);

        Domain domain2 = new Domain("2");
        InstanceType instanceType2 = new InstanceType();
        instanceType2.setMemory(BigDecimal.TEN);
        instanceType2.setDuration(BigDecimal.TEN);
        domain2.getUsage().getCompute().getInstanceTypes().add(instanceType2);
        report.getDomains().add(domain2);

        Domain domain3 = new Domain("3");
        InstanceType instanceType3 = new InstanceType();
        instanceType3.setMemory(BigDecimal.ZERO);
        instanceType3.setDuration(BigDecimal.ZERO);
        domain3.getUsage().getCompute().getInstanceTypes().add(instanceType3);
        report.getDomains().add(domain3);

        // Ascending
        SortingUtils.sort(report, SortBy.MEMORY, SortOrder.ASC);
        Assertions.assertThat(report.getDomains().get(0)).isEqualTo(domain3);
        Assertions.assertThat(report.getDomains().get(1)).isEqualTo(domain1);
        Assertions.assertThat(report.getDomains().get(2)).isEqualTo(domain2);

        // Descending
        SortingUtils.sort(report, SortBy.MEMORY, SortOrder.DESC);
        Assertions.assertThat(report.getDomains().get(0)).isEqualTo(domain2);
        Assertions.assertThat(report.getDomains().get(1)).isEqualTo(domain1);
        Assertions.assertThat(report.getDomains().get(2)).isEqualTo(domain3);
    }

    @Test
    public void sortByStorage() {
        final Report report = new Report();

        Domain domain1 = new Domain("1");
        VolumeSize volumeSize1 = new VolumeSize();
        volumeSize1.setSize(BigDecimal.ONE);
        volumeSize1.setDuration(BigDecimal.ONE);
        domain1.getUsage().getStorage().getVolumeSizes().add(volumeSize1);
        report.getDomains().add(domain1);

        Domain domain2 = new Domain("2");
        VolumeSize volumeSize2 = new VolumeSize();
        volumeSize2.setSize(BigDecimal.TEN);
        volumeSize2.setDuration(BigDecimal.TEN);
        domain2.getUsage().getStorage().getVolumeSizes().add(volumeSize2);
        report.getDomains().add(domain2);

        Domain domain3 = new Domain("3");
        VolumeSize volumeSize3 = new VolumeSize();
        volumeSize3.setSize(BigDecimal.ZERO);
        volumeSize3.setDuration(BigDecimal.ZERO);
        domain3.getUsage().getStorage().getVolumeSizes().add(volumeSize3);
        report.getDomains().add(domain3);

        // Ascending
        SortingUtils.sort(report, SortBy.VOLUME, SortOrder.ASC);
        Assertions.assertThat(report.getDomains().get(0)).isEqualTo(domain3);
        Assertions.assertThat(report.getDomains().get(1)).isEqualTo(domain1);
        Assertions.assertThat(report.getDomains().get(2)).isEqualTo(domain2);

        // Descending
        SortingUtils.sort(report, SortBy.VOLUME, SortOrder.DESC);
        Assertions.assertThat(report.getDomains().get(0)).isEqualTo(domain2);
        Assertions.assertThat(report.getDomains().get(1)).isEqualTo(domain1);
        Assertions.assertThat(report.getDomains().get(2)).isEqualTo(domain3);
    }

    @Test
    public void sortByPublicIp() {
        final Report report = new Report();

        Domain domain1 = new Domain("1");
        report.getDomains().add(domain1);

        Network network1 = new Network();
        network1.setName("network1");
        domain1.getUsage().getNetworking().getNetworks().add(network1);

        PublicIp publicIp1111 = new PublicIp();
        publicIp1111.setValue("1.1.1.1");
        network1.getPublicIps().add(publicIp1111);

        Domain domain2 = new Domain("2");
        report.getDomains().add(domain2);

        Network network2 = new Network();
        network2.setName("network2");
        domain2.getUsage().getNetworking().getNetworks().add(network2);

        PublicIp publicIp1121 = new PublicIp();
        publicIp1121.setValue("1.1.2.1");
        network2.getPublicIps().add(publicIp1121);

        PublicIp publicIp1122 = new PublicIp();
        publicIp1122.setValue("1.1.2.2");
        network2.getPublicIps().add(publicIp1122);

        Domain domain3 = new Domain("3");
        report.getDomains().add(domain3);

        Network network3 = new Network();
        network3.setName("network3");
        domain3.getUsage().getNetworking().getNetworks().add(network3);

        // Ascending
        SortingUtils.sort(report, SortBy.PUBLIC_IP, SortOrder.ASC);
        Assertions.assertThat(report.getDomains().get(0)).isEqualTo(domain3);
        Assertions.assertThat(report.getDomains().get(1)).isEqualTo(domain1);
        Assertions.assertThat(report.getDomains().get(2)).isEqualTo(domain2);

        // Descending
        SortingUtils.sort(report, SortBy.PUBLIC_IP, SortOrder.DESC);
        Assertions.assertThat(report.getDomains().get(0)).isEqualTo(domain2);
        Assertions.assertThat(report.getDomains().get(1)).isEqualTo(domain1);
        Assertions.assertThat(report.getDomains().get(2)).isEqualTo(domain3);
    }
}
