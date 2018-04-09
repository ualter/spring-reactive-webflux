package com.spring.webflux.reactive.client;

import java.time.Duration;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import com.spring.webflux.reactive.model.Vehicle;

public class VehiclesWebClient {

	private WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").build();

	public void vehicleDetected() {
		//@formatter:off
	        webClient.get()
	            .uri("/vehicles")
	            .accept(MediaType.APPLICATION_STREAM_JSON)
	            .exchange()
	            .flatMapMany(response -> response.bodyToFlux(Vehicle.class))
	            .delayElements(Duration.ofMillis(1000))
	            .subscribe(s -> {
	                    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> " + s);
	                },
	                err -> System.out.println("Error on Weather Stream: " + err),
	                () -> System.out.println("Vehicle stream stoped!"));
	      //@formatter:on
	}
	
	public static void main(String[] args) {
		VehiclesWebClient vehiclesWebClient = new VehiclesWebClient();
		vehiclesWebClient.vehicleDetected();
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
