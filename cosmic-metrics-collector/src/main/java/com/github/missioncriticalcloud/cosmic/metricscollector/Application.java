package com.github.missioncriticalcloud.cosmic.metricscollector;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.github.missioncriticalcloud.cosmic.metricscollector.model.Metric;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(final String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JodaModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Bean
    public ObjectWriter objectWriter() {
        return objectMapper().writer().forType(Metric.class);
    }

    @Bean
    public YamlPropertiesFactoryBean queries() {
        final YamlPropertiesFactoryBean yamlProperties = new YamlPropertiesFactoryBean();
        yamlProperties.setResources(new ClassPathResource("/queries.yml"));

        return yamlProperties;
    }

}
