package com.github.missioncriticalcloud.cosmic.api.usage.repositories;

import com.github.missioncriticalcloud.cosmic.usage.core.model.VirtualMachine;

public interface VirtualMachineRepository {

    VirtualMachine get(String uuid);

}
