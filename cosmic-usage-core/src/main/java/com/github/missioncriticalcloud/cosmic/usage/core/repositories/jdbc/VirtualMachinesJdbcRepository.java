package com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc;

import java.util.List;
import java.util.Properties;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Tag;
import com.github.missioncriticalcloud.cosmic.usage.core.model.VirtualMachine;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.VirtualMachinesRepository;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc.mappers.TagMapper;
import com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc.mappers.VirtualMachineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class VirtualMachinesJdbcRepository implements VirtualMachinesRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final Properties queries;
    private final VirtualMachineMapper virtualMachineMapper;
    private final TagMapper tagMapper;

    @Autowired
    public VirtualMachinesJdbcRepository(
            final NamedParameterJdbcTemplate jdbcTemplate,
            @Qualifier("queries") final Properties queries,
            final VirtualMachineMapper virtualMachineMapper,
            final TagMapper tagMapper
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.queries = queries;
        this.virtualMachineMapper = virtualMachineMapper;
        this.tagMapper = tagMapper;
    }

    @Override
    public VirtualMachine get(final String uuid) {
        try {
            final VirtualMachine virtualMachine = jdbcTemplate.queryForObject(
                    queries.getProperty("virtual-machines-repository.get-virtual-machine"),
                    new MapSqlParameterSource("uuid", uuid),
                    virtualMachineMapper
            );

            final List<Tag> tags = jdbcTemplate.query(
                    queries.getProperty("virtual-machines-repository.get-virtual-machine-tags"),
                    new MapSqlParameterSource("uuid", uuid),
                    tagMapper
            );

            virtualMachine.getTags().addAll(tags);

            return virtualMachine;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
