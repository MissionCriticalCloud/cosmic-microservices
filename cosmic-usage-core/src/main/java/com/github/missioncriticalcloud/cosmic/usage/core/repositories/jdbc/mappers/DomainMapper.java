package com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class DomainMapper implements RowMapper<Domain> {

    @Override
    public Domain mapRow(final ResultSet resultSet, final int i) throws SQLException {
        final Domain domain = new Domain(resultSet.getString("uuid"));
        domain.setName(resultSet.getString("name"));
        domain.setPath(resultSet.getString("path"));
        domain.setEmail(resultSet.getString("email"));

        return domain;
    }
}
