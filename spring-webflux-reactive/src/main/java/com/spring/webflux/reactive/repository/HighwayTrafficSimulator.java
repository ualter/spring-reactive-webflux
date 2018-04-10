package com.spring.webflux.reactive.repository;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.spring.webflux.reactive.model.Vehicle;

import reactor.core.publisher.Flux;

@Service
public class HighwayTrafficSimulator implements HighwayTraffic {

    private static Logger LOGGER = LoggerFactory.getLogger(HighwayTrafficSimulator.class);
    private static DecimalFormat plateFormatNumber = new DecimalFormat("0000");
    private static int MINUTES_LIMIT_TIME_HIGHWAY_OPENED = 10;
    
    private LinkedList<Vehicle> traffic;
    private boolean openStream; 
    
    public HighwayTrafficSimulator() {
        this.traffic = new LinkedList<Vehicle>();
        this.openTheHighwayTraffic();
    }
 
    
    private void openTheHighwayTraffic() {
        LocalDateTime startTime = LocalDateTime.now();

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            while (true) {
//                int maxSleep = HighwayUtilities.simulateTrafficIntervals();
//                try {
//                    Thread.sleep(ThreadLocalRandom.current().nextInt(0, maxSleep));
//                } catch (InterruptedException e) {
//                    LOGGER.error(e.getMessage(), e);
//                    throw new RuntimeException(e);
//                }
                
                this.traffic.addLast( HighwayUtilities.simulateTraffic() );
                if ( this.traffic.size() > 3 ) {
                	this.openStream = true;
                }
                
                // Let's put a limit on time, only to not let the infinite loop running (dangerously)
                long timeMinutesHighwayOpened = startTime.until(LocalDateTime.now(), ChronoUnit.MINUTES);
                if (timeMinutesHighwayOpened > MINUTES_LIMIT_TIME_HIGHWAY_OPENED) {
                    break;
                }
            }
        });
        executor.shutdown();
    }

    
    
    public Flux<Vehicle> flowTraffic() {
        return Flux.<Vehicle>create(fluxSink -> {
            while( true ) {
                if ( this.openStream && this.traffic.size() > 0 ) { 
                	fluxSink.next( this.traffic.poll() );
                } else 
                if ( this.openStream && this.traffic.size() < 1 ) {
                	break;
                }
                Thread.yield();
            }
        }).share(); 
    }
   

    public static void main(String[] args) {
        HighwayTrafficSimulator h = new HighwayTrafficSimulator();
        h.flowTraffic().subscribe(v -> System.out.println(v));
        System.out.println("###");
        
    }
    
    private static class HighwayUtilities {
    	
    	private static int simulateTrafficIntervals() {
            int maxSleep = 0;
            int moviment = ThreadLocalRandom.current()
                .nextInt(1, 5);
            switch (moviment) {
            case 1:
                maxSleep = 1;
                break;
            case 2:
                maxSleep = 75;
                break;
            case 3:
                maxSleep = 1000;
                break;
            case 4:
                maxSleep = 50;
                break;
            case 5:
                maxSleep = 200;
                break;
            default:
                break;
            }
            return maxSleep;
        }

        private static Vehicle simulateTraffic() {
            RandomStringGenerator rndStringGen = new RandomStringGenerator.Builder().withinRange('A', 'Z')
                .build();

            StringBuffer sb = new StringBuffer(rndStringGen.generate(3)
                .toUpperCase());
            sb.append(" ");
            sb.append(plateFormatNumber.format(ThreadLocalRandom.current()
                .nextInt(0, 9999)));
            String carPlateNumber = sb.toString();

            Long weight = ThreadLocalRandom.current()
                .nextLong(250L, 4500L);
            Integer speed = ThreadLocalRandom.current()
                .nextInt(60, 175);

            return new Vehicle(carPlateNumber, weight, speed);
        }
    	
    }

}
