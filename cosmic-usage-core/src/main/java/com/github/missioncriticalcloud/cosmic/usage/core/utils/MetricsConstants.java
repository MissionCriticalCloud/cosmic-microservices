package com.github.missioncriticalcloud.cosmic.usage.core.utils;

public class MetricsConstants {
    public static final int MAX_DOMAIN_AGGREGATIONS = 250;
    public static final int MAX_RESOURCE_AGGREGATIONS = 2500;

    public static final String DOMAINS_AGGREGATION = "domains";
    public static final String RESOURCES_AGGREGATION = "resources";
    public static final String PAYLOAD_AGGREGATION = "payload";

    public static final String CPU_AGGREGATION = "cpu";
    public static final String MEMORY_AGGREGATION = "memory";
    public static final String VOLUME_AGGREGATION = "volume";

    public static final String TIMESTAMP_FIELD = "@timestamp";
    public static final String RESOURCE_TYPE_FIELD = "resourceType";
    public static final String DOMAIN_UUID_FIELD = "domainUuid";
    public static final String RESOURCE_UUID_FIELD = "resourceUuid";

    public static final String PAYLOAD_CPU_FIELD = "payload.cpu";
    public static final String PAYLOAD_MEMORY_FIELD = "payload.memory";
    public static final String PAYLOAD_SIZE_FIELD = "payload.size";

    public static final String PAYLOAD_PATH = "payload";
}
