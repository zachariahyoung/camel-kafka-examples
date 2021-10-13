package com.zandriod.healthcheck.controller;

import com.zandriod.healthcheck.dto.ResilienceDto;
import com.zandriod.healthcheck.service.ResilienceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class ResilienceController {

    public final ResilienceService resilienceService;

    public ResilienceController(ResilienceService resilienceService) {
        this.resilienceService = resilienceService;
    }

    @GetMapping("/resilience")
    public Mono<ResponseEntity<ResilienceDto>> getResilience(){
        return resilienceService.fetchResilience()
                .map(resilienceDto -> ResponseEntity.ok(resilienceDto))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/exception")
    public Mono<ResponseEntity<ResilienceDto>> getException(){
        return resilienceService.fetchException()
                .map(resilienceDto -> ResponseEntity.ok(resilienceDto))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/delay")
    public Mono<ResponseEntity<ResilienceDto>> getDelay(){
        return resilienceService.fetchResilience()
                .delayElement(Duration.ofSeconds(3))
                .map(resilienceDto -> ResponseEntity.ok(resilienceDto))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
