package com.github.missioncriticalcloud.cosmic.metricscollector.repositories.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.missioncriticalcloud.cosmic.metricscollector.model.Metric;
import com.github.missioncriticalcloud.cosmic.metricscollector.model.ResourceType;

public class VirtualMachineMetricsMapper extends MetricsMapper {

    @Override
    public Metric mapRow(final ResultSet resultSet, final int i) throws SQLException {
        final Metric metric = super.mapRow(resultSet, i);

        metric.setResourceType(ResourceType.VIRTUAL_MACHINE);
        metric.getPayload().put("state", resultSet.getString("state"));
        metric.getPayload().put("cpu", resultSet.getInt("cpu"));
        metric.getPayload().put("memory", resultSet.getInt("memory"));

        return metric;
    }

}
