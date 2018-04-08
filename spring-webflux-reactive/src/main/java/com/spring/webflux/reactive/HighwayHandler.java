package com.spring.webflux.reactive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.spring.webflux.reactive.model.Vehicle;
import com.spring.webflux.reactive.repository.HighwayReactiveRepositoryImpl;
import com.spring.webflux.reactive.repository.HighwayTrafficSimulator;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class HighwayHandler {
    
    @Autowired
    private HighwayReactiveRepositoryImpl highwayRepository;
    
    public Mono<ServerResponse> getVehicleDetected(ServerRequest request) {
        //Flux<Vehicle> vehicle = highwayRepository.vechicleDetected();
        
        
        HighwayTrafficSimulator h = new HighwayTrafficSimulator();
        ConnectableFlux<Vehicle> publisher = Flux.<Vehicle>create(fluxSink -> {
            while(true) {
                fluxSink.next(h.generateRandomVehicle());
            }
        })
          //.sample(Duration.ofMillis(1))
          .log()
          //.subscribeOn(Schedulers.parallel())
          .publish();

        
        
        publisher.subscribe( v -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(v,Vehicle.class) );
        publisher.connect();
        
        
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(vehicle,Vehicle.class);
        //return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromObject(vehicle));
    }
    

}
