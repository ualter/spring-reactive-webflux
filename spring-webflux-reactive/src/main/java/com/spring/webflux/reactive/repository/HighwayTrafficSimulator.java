package com.spring.webflux.reactive.repository;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spring.webflux.reactive.model.Vehicle;

import reactor.core.publisher.Flux;

public class HighwayTrafficSimulator {

    private static Logger LOGGER = LoggerFactory.getLogger(HighwayTrafficSimulator.class);
    private static DecimalFormat plateFormatNumber = new DecimalFormat("0000");
    
    private static int MINUTES_LIMIT_TIME_OPENED = 5;
    
    private LinkedList<Vehicle> traffic;
    private boolean startConsume = false;
    
    public HighwayTrafficSimulator() {
        this.traffic = new LinkedList<Vehicle>();
        this.openTheHighwayTraffic();
    }
 
    
    private void openTheHighwayTraffic() {
        LocalDateTime startTime = LocalDateTime.now();

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(() -> {
            while (true) {
                int maxSleep = simulateTrafficIntervals();
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(0, maxSleep));
                } catch (InterruptedException e) {
                    LOGGER.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                }
                
                this.traffic.addLast( simulateTraffic() );
                // Wait for at least 5 coches crossing
                if ( this.traffic.size() > 5 ) {
                    this.startConsume = true;
                }
                
                // Let's put a limit on time, only to do not let the infinite loop running
                long timeMinutesHighwayOpened = startTime.until(LocalDateTime.now(), ChronoUnit.MINUTES);
                if (timeMinutesHighwayOpened > MINUTES_LIMIT_TIME_OPENED) {
                    break;
                }
            }
        });
        executor.shutdown();
    }

    private int simulateTrafficIntervals() {
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

    private Vehicle simulateTraffic() {
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
    
    public Flux<Vehicle> flowTraffic() {
        return Flux.<Vehicle>create(fluxSink -> {
            while( true ) {
                if ( this.traffic.size() > 0 ) { 
              fluxSink.next( this.traffic.poll() );
                }
            }
        }).share(); 
    }

   

    public static void main(String[] args) {
        HighwayTrafficSimulator h = new HighwayTrafficSimulator();
        h.flowTraffic().subscribe(v -> System.out.println(v));
        
        /*try {
            Thread.sleep(1000);
            while(true) {
                //Thread.sleep(1);
                h.getTraffic().forEach(System.out::println);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
        
    }

}
