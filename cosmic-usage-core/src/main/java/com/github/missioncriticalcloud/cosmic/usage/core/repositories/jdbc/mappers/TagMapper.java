package com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Tag;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class TagMapper implements RowMapper<Tag> {

    @Override
    public Tag mapRow(final ResultSet resultSet, final int i) throws SQLException {
        final Tag tag = new Tag();
        tag.setUuid(resultSet.getString("uuid"));
        tag.setKey(resultSet.getString("key"));
        tag.setValue(resultSet.getString("value"));

        return tag;
    }
}
