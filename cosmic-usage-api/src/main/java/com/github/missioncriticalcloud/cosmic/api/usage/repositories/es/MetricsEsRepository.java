package com.github.missioncriticalcloud.cosmic.api.usage.repositories.es;

import java.io.IOException;

import com.github.missioncriticalcloud.cosmic.api.usage.repositories.MetricsRepository;
import com.github.missioncriticalcloud.cosmic.usage.core.exceptions.UnableToSearchMetricsException;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public abstract class MetricsEsRepository implements MetricsRepository {
    private final JestClient client;

    public MetricsEsRepository(final JestClient client) {
        this.client = client;
    }

    SearchResult search(final SearchSourceBuilder searchBuilder) {
        try {
            return client.execute(
                    new Search.Builder(searchBuilder.toString())
                            .addIndex("cosmic-metrics-*")
                            .addType("metric")
                            .build()
            );
        } catch (IOException e) {
            throw new UnableToSearchMetricsException(e.getMessage(), e);
        }
    }
}
