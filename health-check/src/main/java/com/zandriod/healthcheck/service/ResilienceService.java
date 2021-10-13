package com.zandriod.healthcheck.service;

import com.zandriod.healthcheck.dto.ResilienceDto;
import com.zandriod.healthcheck.exception.NonRecoverableException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ResilienceService {

    public Mono<ResilienceDto> fetchResilience(){
        ResilienceDto resilienceDto = new ResilienceDto();
        resilienceDto.setStatus("Hello World!");
        return Mono.just(resilienceDto);
    }
    public Mono<ResilienceDto> fetchException(){
        throw new NonRecoverableException("Non Recoverable");
    }
}
