package com.github.missioncriticalcloud.cosmic.usage.core.repositories;

import java.util.List;
import java.util.Map;

import com.github.missioncriticalcloud.cosmic.usage.core.model.Domain;

public interface DomainsRepository {

    List<Domain> search(String path);

    Domain get(String path);

    Domain getByUuid(final String uuid);

    Map<String, Domain> map(String path, boolean detailed);
}
