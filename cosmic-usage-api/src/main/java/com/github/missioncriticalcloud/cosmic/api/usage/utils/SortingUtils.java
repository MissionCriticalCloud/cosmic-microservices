package com.github.missioncriticalcloud.cosmic.api.usage.utils;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Report;
import com.github.missioncriticalcloud.cosmic.usage.core.model.Usage;

public class SortingUtils {

    public enum SortBy {
        DOMAIN_PATH, CPU, MEMORY, VOLUME, PUBLIC_IP;
        public static final String DEFAULT = "DOMAIN_PATH";
    }

    public enum SortOrder {
        ASC, DESC;
        public static final String DEFAULT = "ASC";
    }

    private SortingUtils() {
        // Empty constructor
    }

    public static void sort(final Report report, final SortBy sortBy, final SortOrder sortOrder) {
        report.getDomains().sort((domain1, domain2) -> {
            final Usage usage1 = domain1.getUsage();
            final Usage usage2 = domain2.getUsage();

            switch (sortBy) {
                case DOMAIN_PATH:
                    return (SortOrder.DESC.equals(sortOrder))
                            ? domain2.getPath()
                                     .compareToIgnoreCase(domain1.getPath())
                            : domain1.getPath()
                                     .compareToIgnoreCase(domain2.getPath());
                case CPU:
                    return (SortOrder.DESC.equals(sortOrder))
                            ? (usage2.getCompute().getTotal().stream().mapToInt(total -> (total.getCpu().multiply(total.getDuration())).intValue()).sum()
                            - usage1.getCompute().getTotal().stream().mapToInt(total -> (total.getCpu().multiply(total.getDuration())).intValue()).sum())
                            : (usage1.getCompute().getTotal().stream().mapToInt(total -> (total.getCpu().multiply(total.getDuration())).intValue()).sum()
                            - usage2.getCompute().getTotal().stream().mapToInt(total -> (total.getCpu().multiply(total.getDuration())).intValue()).sum());
                case MEMORY:
                    return (SortOrder.DESC.equals(sortOrder))
                            ? (usage2.getCompute().getTotal().stream().mapToInt(total -> (total.getMemory().multiply(total.getDuration())).intValue()).sum()
                            - usage1.getCompute().getTotal().stream().mapToInt(total -> (total.getMemory().multiply(total.getDuration())).intValue()).sum())
                            : (usage1.getCompute().getTotal().stream().mapToInt(total -> (total.getMemory().multiply(total.getDuration())).intValue()).sum()
                            - usage2.getCompute().getTotal().stream().mapToInt(total -> (total.getMemory().multiply(total.getDuration())).intValue()).sum());
                case VOLUME:
                    return (SortOrder.DESC.equals(sortOrder))
                            ? (usage2.getStorage().getTotal().stream().mapToInt(total -> (total.getSize().multiply(total.getDuration())).intValue()).sum()
                            - usage1.getStorage().getTotal().stream().mapToInt(total -> (total.getSize().multiply(total.getDuration())).intValue()).sum())
                            : (usage1.getStorage().getTotal().stream().mapToInt(total -> (total.getSize().multiply(total.getDuration())).intValue()).sum()
                            - usage2.getStorage().getTotal().stream().mapToInt(total -> (total.getSize().multiply(total.getDuration())).intValue()).sum());
                case PUBLIC_IP:
                    return (SortOrder.DESC.equals(sortOrder))
                            ? usage2.getNetworking().getTotal().getPublicIps()
                                    .compareTo(usage1.getNetworking().getTotal().getPublicIps())
                            : usage1.getNetworking().getTotal().getPublicIps()
                                    .compareTo(usage2.getNetworking().getTotal().getPublicIps());
                default:
                    return 0;
            }
        });
    }
}
