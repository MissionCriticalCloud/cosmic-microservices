package com.github.missioncriticalcloud.cosmic.billingreporter;

import com.github.missioncriticalcloud.cosmic.billingreporter.services.BillingReporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

    BillingReporter billingReporter;

    @Autowired
    Application(final BillingReporter billingReporter) {
        this.billingReporter = billingReporter;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(final String... strings) throws Exception {

    }
}
