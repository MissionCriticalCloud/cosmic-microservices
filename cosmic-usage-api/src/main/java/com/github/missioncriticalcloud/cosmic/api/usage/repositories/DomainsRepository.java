package com.github.missioncriticalcloud.cosmic.api.usage.repositories;

import javax.validation.constraints.NotNull;
import java.util.List;

import com.github.missioncriticalcloud.cosmic.api.usage.model.Domain;

public interface DomainsRepository {

    List<Domain> list(@NotNull String path);

}
