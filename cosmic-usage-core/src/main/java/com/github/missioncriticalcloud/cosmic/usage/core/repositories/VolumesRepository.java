package com.github.missioncriticalcloud.cosmic.usage.core.repositories;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Volume;

public interface VolumesRepository {

    Volume get(String uuid);
}
