package com.spring.webflux.reactive.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.spring.webflux.reactive.model.Vehicle;

import reactor.core.publisher.Flux;

@Component
public class HighwayReactiveRepositoryImpl implements HighwayReactiveRepository {

    private static Logger LOGGER = LoggerFactory.getLogger(HighwayReactiveRepositoryImpl.class);
    
    public Flux<Vehicle> vechicleDetected() {
        
        
    }


}
