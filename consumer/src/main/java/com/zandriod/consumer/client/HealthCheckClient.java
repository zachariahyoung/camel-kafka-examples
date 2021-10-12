package com.zandriod.consumer.client;

import com.zandriod.consumer.dto.Health;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class HealthCheckClient {

    private final WebClient healthCheckClient;

    public HealthCheckClient(WebClient healthCheckClient) {
        this.healthCheckClient = healthCheckClient;
    }

    public Mono<Health> fetchStatus() {
        return healthCheckClient
                .get()
                .uri("http://localhost:8091/check/actuator/health")
                .retrieve()
                .bodyToMono(Health.class);
    }
}
