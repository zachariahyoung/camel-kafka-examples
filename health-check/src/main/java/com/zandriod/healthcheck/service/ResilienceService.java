package com.zandriod.healthcheck.service;

import com.zandriod.healthcheck.dto.ResilienceDto;
import com.zandriod.healthcheck.exception.NonRecoverableException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ResilienceService {

    public Mono<ResilienceDto> fetchResilience(){
        return Mono.just(new ResilienceDto("Hello World!"));
    }
    public Mono<ResilienceDto> fetchException(){
        throw new NonRecoverableException("Non Recoverable");
    }
}
