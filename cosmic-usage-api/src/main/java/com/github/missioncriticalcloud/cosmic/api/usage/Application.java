package com.github.missioncriticalcloud.cosmic.api.usage;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

@SpringBootApplication
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public YamlPropertiesFactoryBean queries() {
        final YamlPropertiesFactoryBean yamlProperties = new YamlPropertiesFactoryBean();
        yamlProperties.setResources(new ClassPathResource("/queries.yml"));

        return yamlProperties;
    }

}
