package com.github.missioncriticalcloud.cosmic.billingreporter.repositories;

import java.util.List;

import org.joda.time.DateTime;

public interface DomainsRepository {

    List<String> listDomains(final DateTime from, final DateTime to);
}
