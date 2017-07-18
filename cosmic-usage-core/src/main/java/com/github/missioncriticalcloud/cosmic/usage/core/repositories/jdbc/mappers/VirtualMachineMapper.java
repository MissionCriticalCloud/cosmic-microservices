package com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.missioncriticalcloud.cosmic.usage.core.model.VirtualMachine;
import com.github.missioncriticalcloud.cosmic.usage.core.model.types.OsType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class VirtualMachineMapper implements RowMapper<VirtualMachine> {

    @Override
    public VirtualMachine mapRow(final ResultSet resultSet, final int i) throws SQLException {
        final VirtualMachine virtualMachine = new VirtualMachine();
        virtualMachine.setUuid(resultSet.getString("uuid"));
        virtualMachine.setHostname(resultSet.getString("hostname"));
        virtualMachine.setOsType(OsType.valueOf(resultSet.getString("osType")));

        return virtualMachine;
    }
}
