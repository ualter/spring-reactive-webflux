package com.spring.webflux.reactive.repository;

import com.spring.webflux.reactive.model.Vehicle;

import reactor.core.publisher.Flux;

public interface HighwayReactiveRepository {
    
    public Flux<Vehicle> vechicleDetected();

}
