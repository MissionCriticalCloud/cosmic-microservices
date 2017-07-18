package com.github.missioncriticalcloud.cosmic.usage.core;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class UsageCoreConfiguration {

    @Bean
    public YamlPropertiesFactoryBean queries() {
        final YamlPropertiesFactoryBean yamlProperties = new YamlPropertiesFactoryBean();
        yamlProperties.setResources(new ClassPathResource("/queries.yml"));

        return yamlProperties;
    }
}
