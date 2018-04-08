package com.spring.webflux.reactive.repository;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spring.webflux.reactive.model.Vehicle;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

public class HighwayTrafficSimulator {

    private static Logger LOGGER = LoggerFactory.getLogger(HighwayTrafficSimulator.class);
    private static DecimalFormat plateFormatNumber = new DecimalFormat("0000");
    
    private Consumer<Vehicle> consumer;

    public HighwayTrafficSimulator() {
        this.openTheHighwayTraffic();
    }
 
    
    public void vehicleCrossed(Consumer<Vehicle> consumer) {
        this.consumer = consumer;
    }
    /**
     * Not used (for now)
     */
    private void openTheHighwayTraffic() {
        LocalDateTime startTime = LocalDateTime.now();

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            while (true) {
                int maxSleep = simulateTrafficPace();
                try {
                    Thread.sleep(ThreadLocalRandom.current()
                        .nextInt(0, maxSleep));
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }

                Vehicle vehicle = generateRandomVehicle();
                consumer.accept(vehicle);

                // Let's put a limit on time, only to do not let the infinite loop running
                long timeMinutesHighwayOpened = startTime.until(LocalDateTime.now(), ChronoUnit.MINUTES);
                if (timeMinutesHighwayOpened > 5) {
                    break;
                }
            }
        });
        executor.shutdown();
    }

    private int simulateTrafficPace() {
        int maxSleep = 0;
        int moviment = ThreadLocalRandom.current()
            .nextInt(1, 5);
        switch (moviment) {
        case 1:
            maxSleep = 1;
            break;
        case 2:
            maxSleep = 150;
            break;
        case 3:
            maxSleep = 1000;
            break;
        case 4:
            maxSleep = 50;
            break;
        case 5:
            maxSleep = 10;
            break;
        default:
            break;
        }
        return maxSleep;
    }

    public Vehicle generateRandomVehicle() {
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

        Vehicle vehicle = new Vehicle(carPlateNumber, weight, speed);
        return vehicle;
    }

   

    public static void main(String[] args) {
        HighwayTrafficSimulator h = new HighwayTrafficSimulator();
        ConnectableFlux<Vehicle> publisher = Flux.<Vehicle>create(fluxSink -> {
            while(true) {
                fluxSink.next(h.generateRandomVehicle());
            }
        })
          //.sample(Duration.ofMillis(1))
          //.log()
          //.subscribeOn(Schedulers.parallel())
          .publish();
        
        
        
        
        
        publisher
         .log()
         .subscribe(v -> System.out.println(v));
        
        publisher.connect();
        
        
        
        /*HighwayTrafficSimulator h = new HighwayTrafficSimulator();
        Mono<Vehicle> s = Mono.<Vehicle>create(sink -> {
            sink.success(h.generateRandomVehicle());
        });
        s.subscribe(System.out::println);
        s.subscribe(System.out::println);
        
        s.subscribe(System.out::println);*/
        
    }

}
