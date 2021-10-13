package com.zandriod.consumer.client;

import com.zandriod.consumer.dto.Health;
import com.zandriod.consumer.exception.NonRecoverableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.ConnectException;

@Service
@Slf4j
public class HealthCheckClient {

    private final WebClient healthCheckClient;

    public HealthCheckClient(WebClient healthCheckClient) {
        this.healthCheckClient = healthCheckClient;
    }

    public Mono<Health> fetchStatus() {
        return healthCheckClient
                .get()
                .uri("http://localhost:8092/check/actuator/health")
                .retrieve()
                .bodyToMono(Health.class)
                .onErrorMap(throwable -> throwable.getCause().getClass().equals(ConnectException.class), throwable -> new NonRecoverableException("host is down"));
    }
}
