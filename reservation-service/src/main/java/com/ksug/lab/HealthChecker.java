package com.ksug.lab;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class HealthChecker implements HealthIndicator {

    @Override
    public Health health() {
        boolean isOK = true;

        if(!isOK) return Health.down().withDetail("Error Code", 100000).build();

        return Health.up().build();
    }

}
