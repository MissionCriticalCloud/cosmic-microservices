package com.github.missioncriticalcloud.cosmic.billingreporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.github.missioncriticalcloud.cosmic.billingreporter", "com.github.missioncriticalcloud.cosmic.usage.core"})
@EnableScheduling
public class Application {

    public static void main(final String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
