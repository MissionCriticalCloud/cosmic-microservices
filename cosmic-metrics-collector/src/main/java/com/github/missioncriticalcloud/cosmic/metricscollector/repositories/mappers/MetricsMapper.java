package com.github.missioncriticalcloud.cosmic.metricscollector.repositories.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Metric;
import org.joda.time.DateTime;
import org.springframework.jdbc.core.RowMapper;

public abstract class MetricsMapper implements RowMapper<Metric> {

    @Override
    public Metric mapRow(final ResultSet resultSet, final int i) throws SQLException {
        final Metric metric = new Metric();

        metric.setDomainUuid(resultSet.getString("domainUuid"));
        metric.setResourceUuid(resultSet.getString("resourceUuid"));

        metric.setTimestamp(new DateTime());

        return metric;
    }

}
