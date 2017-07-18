package com.github.missioncriticalcloud.cosmic.usage.core.repositories;

import com.github.missioncriticalcloud.cosmic.usage.core.model.VirtualMachine;

public interface VirtualMachinesRepository {

    VirtualMachine get(String uuid);
}
