package com.github.missioncriticalcloud.cosmic.usage.core.repositories;

import com.github.missioncriticalcloud.cosmic.usage.core.model.PublicIp;

public interface PublicIpsRepository {

    PublicIp get(String uuid);
}
