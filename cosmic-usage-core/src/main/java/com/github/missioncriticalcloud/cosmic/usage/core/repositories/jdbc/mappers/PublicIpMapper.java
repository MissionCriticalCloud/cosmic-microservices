package com.github.missioncriticalcloud.cosmic.usage.core.repositories.jdbc.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Network;
import com.github.missioncriticalcloud.cosmic.usage.core.model.PublicIp;
import com.github.missioncriticalcloud.cosmic.usage.core.model.types.NetworkType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class PublicIpMapper implements RowMapper<PublicIp> {

    @Override
    public PublicIp mapRow(final ResultSet resultSet, final int i) throws SQLException {
        final PublicIp publicIp = new PublicIp();
        publicIp.setUuid(resultSet.getString("uuid"));
        publicIp.setValue(resultSet.getString("ip"));

        final String vpcUuid = resultSet.getString("vpcUuid");
        final String networkUuid = resultSet.getString("networkUuid");

        if (vpcUuid != null) {
            final Network network = new Network();
            network.setUuid(vpcUuid);
            network.setName(resultSet.getString("vpcName"));
            network.setType(NetworkType.VPC);
            publicIp.setNetwork(network);
            publicIp.setState(
                    networkUuid != null
                            ? PublicIp.State.ATTACHED
                            : PublicIp.State.DETACHED
            );
        } else {
            final Network network = new Network();
            network.setUuid(networkUuid);
            network.setName(resultSet.getString("networkName"));
            network.setType(NetworkType.GUEST);
            publicIp.setNetwork(network);
            publicIp.setState(PublicIp.State.ATTACHED);
        }

        return publicIp;
    }
}
