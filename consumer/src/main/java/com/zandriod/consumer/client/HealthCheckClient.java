package com.zandriod.consumer.client;

import com.zandriod.consumer.dto.ResilienceDto;
import com.zandriod.consumer.exception.BadClientRequestException;
import com.zandriod.consumer.exception.NonRecoverableException;
import com.zandriod.consumer.exception.RecoverableException;
import com.zandriod.consumer.util.ResponseValidationUtil;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
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

    public Mono<ResilienceDto> fetchStatus() {
        return healthCheckClient
                .get()
                .uri("http://localhost:8092/check/actuator/health")
                .retrieve()
                .bodyToMono(ResilienceDto.class)
                .onErrorMap(throwable -> throwable.getCause().getClass().equals(ConnectException.class), throwable -> new NonRecoverableException("host is down"));
    }

//    @Retry(name = "check", fallbackMethod = "failedCheckRetry")
    public Mono<ResilienceDto> fetchResilience() {
        return healthCheckClient
                .get()
                .uri("http://localhost:8092/check/resilience")
                .retrieve()
                .bodyToMono(ResilienceDto.class);
    }

//    @Retry(name = "check", fallbackMethod = "failedCheckRetry")
    public Mono<ResilienceDto> fetchException() {
        return healthCheckClient
                .get()
                .uri("http://localhost:8092/check/exception")
                .retrieve()
                .bodyToMono(ResilienceDto.class);
    }

//    @Retry(name = "check", fallbackMethod = "failedCheckRetry")
    public Mono<ResilienceDto> fetchDelay() {
        return healthCheckClient
                .get()
                .uri("http://localhost:8092/check/delay")
                .retrieve()
                .bodyToMono(ResilienceDto.class);
    }

    public Mono<ResilienceDto> fetchGateway() {
        return healthCheckClient
                .get()
                .uri("/gateway")
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> {
                    ResponseValidationUtil.codeValidation(clientResponse.rawStatusCode());
                    throw new BadClientRequestException("Error while calling health service with response code " + clientResponse.rawStatusCode(), clientResponse.rawStatusCode());
                })
                .bodyToMono(ResilienceDto.class)
                .transformDeferred(RetryOperator.of(Retry.ofDefaults("check")))
                .onErrorResume(RecoverableException.class, this::failedCheckRetry);

    }

    public Mono<ResilienceDto> failedCheckRetry(RecoverableException recoverableException){
       return Mono.error(new NonRecoverableException(recoverableException.getMessage()));
    }


}
