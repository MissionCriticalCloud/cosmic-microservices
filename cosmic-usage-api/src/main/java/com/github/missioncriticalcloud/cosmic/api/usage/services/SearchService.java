package com.github.missioncriticalcloud.cosmic.api.usage.services;

import com.github.missioncriticalcloud.cosmic.api.usage.model.SearchResult;
import org.joda.time.DateTime;

public interface SearchService {

    SearchResult search(DateTime from, DateTime to, String path);

}
