package com.spring.webflux.reactive;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.spring.webflux.reactive.model.Vehicle;
import com.spring.webflux.reactive.repository.HighwayReactiveRepositoryImpl;
import com.spring.webflux.reactive.repository.HighwayTrafficSimulator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class HighwayHandler {
    
    @Autowired
    private HighwayReactiveRepositoryImpl highwayRepository;
    
    public Mono<ServerResponse> vehicleDetected(ServerRequest request) {
    	
    	HighwayTrafficSimulator highwayTrafficSimulator = new HighwayTrafficSimulator();
    	
    	
/*//    	Flux.interval(Duration.ofMillis(500))
//    	    .flatMap(index -> )
*/    	
    	    
    	
    	Flux<Vehicle> publisher = Flux.<Vehicle>create(fluxSink -> {
            int index = 0;
            while( true ) {
              fluxSink.next(highwayTrafficSimulator.generateRandomVehicle());
              index++;
            }
        }).share();    
        //}).publish().autoConnect();
        //}).delayElements(Duration.ofMillis(1000)).share();    
        
        return ServerResponse.ok()
            .contentType(MediaType.APPLICATION_STREAM_JSON)
            .body(publisher,Vehicle.class);
        
    }
    

}
