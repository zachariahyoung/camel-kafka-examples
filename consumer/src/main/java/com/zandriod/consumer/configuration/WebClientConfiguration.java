package com.zandriod.consumer.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class WebClientConfiguration {

    @Bean
    public WebClient webClientCreator() {

        return WebClient.builder()
                .filter(loggingFilter())
                .baseUrl("http://localhost:8092/check").build();
    }

    private static ExchangeFilterFunction loggingFilter() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("Sending request " + request.method() + " " + request.url());
            return Mono.just(request);
        });
    }
}
