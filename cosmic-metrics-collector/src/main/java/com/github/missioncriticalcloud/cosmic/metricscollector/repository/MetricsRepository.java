package com.github.missioncriticalcloud.cosmic.metricscollector.repository;

import java.util.List;

import com.github.missioncriticalcloud.cosmic.metricscollector.model.Metric;

public interface MetricsRepository {

    List<Metric> getMetrics();

}
