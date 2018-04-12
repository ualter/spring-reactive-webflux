package com.spring.webflux.reactive.client;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.spring.webflux.reactive.model.Vehicle;

public class HighwayWebClient {

	private WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").build();

	public void vehicleDetected() {
		AtomicInteger counter = new AtomicInteger(0);
		//@formatter:off
	        webClient.get()
	            .uri("/vehicles")
	            .accept(MediaType.APPLICATION_STREAM_JSON)
	            .exchange()
	            //.publishOn(Schedulers.single())
	            .flatMapMany(response -> response.bodyToFlux(Vehicle.class))
	            //.delayElements(Duration.ofMillis(1))
	            .subscribe(s -> {
	                    System.out.println(counter.incrementAndGet() + " >>>>>>>>>>>>> " + s);
	                },
	                err -> System.out.println("Error on Weather Stream: " + err),
	                () -> System.out.println("Vehicle stream stoped!"));
	      //@formatter:on

	}
	
	public void vehicleHigherThen120Detected() {
		AtomicInteger counter = new AtomicInteger(0);
		//@formatter:off
	        webClient.get()
	            .uri("/vehicles")
	            .accept(MediaType.APPLICATION_STREAM_JSON)
	            .exchange()
	            .flatMapMany(response -> response.bodyToFlux(Vehicle.class))
	            .filter(v -> v.getSpeed() > 120)
	            //.delayElements(Duration.ofMillis(1))
	            .subscribe(s -> {
	                    System.out.println(counter.incrementAndGet() + " >>>>>>>>>>>>> " + s);
	                },
	                err -> System.out.println("Error on Weather Stream: " + err),
	                () -> System.out.println("Vehicle stream stoped!"));
	      //@formatter:on

	}

	public static void main(String[] args) {
		HighwayWebClient vehiclesWebClient = new HighwayWebClient();
		//vehiclesWebClient.vehicleHigherThen120Detected();
		vehiclesWebClient.vehicleDetected();
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
