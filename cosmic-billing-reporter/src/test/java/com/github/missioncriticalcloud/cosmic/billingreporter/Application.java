package com.github.missioncriticalcloud.cosmic.billingreporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.github.missioncriticalcloud.cosmic.billingreporter", "com.github.missioncriticalcloud.cosmic.usage.core"})
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
