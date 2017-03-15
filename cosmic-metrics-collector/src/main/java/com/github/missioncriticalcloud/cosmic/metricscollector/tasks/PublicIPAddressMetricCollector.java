package com.github.missioncriticalcloud.cosmic.metricscollector.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.github.missioncriticalcloud.cosmic.metricscollector.exceptions.FailedToCollectMetricsException;
import com.github.missioncriticalcloud.cosmic.metricscollector.model.Metric;
import com.github.missioncriticalcloud.cosmic.metricscollector.repositories.PublicIPAddressMetricsRepository;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by ikrstic on 14/03/2017.
 */
@Component
public class PublicIPAddressMetricCollector implements MetricCollector{
    private static final Logger LOG = Logger.getLogger(PublicIPAddressMetricCollector.class.getName());

    private final PublicIPAddressMetricsRepository publicIPAddressMetricsRepository;
    private final AmqpTemplate amqpTemplate;
    private final ObjectWriter metricWriter;

    private final String brokerExchange;
    private final String brokerExchangeKey;

    @Autowired
    public PublicIPAddressMetricCollector(
            final PublicIPAddressMetricsRepository publicIPAddressMetricsRepository,
            final AmqpTemplate amqpTemplate,
            final ObjectWriter metricWriter,
            @Value("${cosmic.metrics-collector.broker-exchange:'cosmic-metrics-exchange'}")
            final String brokerExchange,
            @Value("${cosmic.metrics-collector.broker-exchange-key:'cosmic-metrics-key'}")
            final String brokerExchangeKey
    ) {
        this.publicIPAddressMetricsRepository = publicIPAddressMetricsRepository;
        this.amqpTemplate = amqpTemplate;
        this.metricWriter = metricWriter;

        this.brokerExchange = brokerExchange;
        this.brokerExchangeKey = brokerExchangeKey;
    }

    @Scheduled(cron = "${cosmic.metrics-collector.scan-interval:'0 */15 * * * *'}")
    public void run() {
        final StopWatch stopWatch = new StopWatch(VirtualMachineMetricCollector.class.getSimpleName());

        stopWatch.start("Collecting public IP addresses metrics from the database");
        final List<Metric> metrics = publicIPAddressMetricsRepository.getMetrics();
        stopWatch.stop();

        stopWatch.start("Sending metrics to the message queue");
        metrics.forEach(metric -> {
            try {
                amqpTemplate.convertAndSend(
                        brokerExchange,
                        brokerExchangeKey,
                        metricWriter.writeValueAsString(metric)
                );
            } catch (final JsonProcessingException e) {
                throw new FailedToCollectMetricsException(e.getMessage(), e);
            }
        });
        stopWatch.stop();

        LOG.info(stopWatch.prettyPrint());
        LOG.info(String.format("Collected %d public IP addresses metrics.", metrics.size()));

    }
}
