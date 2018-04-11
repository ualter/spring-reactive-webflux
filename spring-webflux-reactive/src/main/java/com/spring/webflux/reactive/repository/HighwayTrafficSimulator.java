package com.spring.webflux.reactive.repository;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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

	public HighwayTrafficSimulator() {
	}

	public Flux<Vehicle> flowTraffic() {
		LocalDateTime startTime = LocalDateTime.now();

		return Flux.<Vehicle>create(fluxSink -> {
			boolean inFrameTime = true;
			int index = 1;
			while ( index <= 20000 && inFrameTime ) {
				fluxSink.next(HighwayUtilities.simulateTraffic());
				index++;

				long timeMinutesHighwayOpened = startTime.until(LocalDateTime.now(), ChronoUnit.MILLIS);
				if ( timeMinutesHighwayOpened > 10000 ) {
					LOGGER.info("TrafficSimulator finish --> With timer");
					inFrameTime = false;
				}
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
			int moviment = ThreadLocalRandom.current().nextInt(1, 5);
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
			RandomStringGenerator rndStringGen = new RandomStringGenerator.Builder().withinRange('A', 'Z').build();

			StringBuffer sb = new StringBuffer(rndStringGen.generate(3).toUpperCase());
			sb.append(" ");
			sb.append(plateFormatNumber.format(ThreadLocalRandom.current().nextInt(0, 9999)));
			String carPlateNumber = sb.toString();

			Long weight = ThreadLocalRandom.current().nextLong(250L, 4500L);
			Integer speed = ThreadLocalRandom.current().nextInt(60, 175);

			return new Vehicle(carPlateNumber, weight, speed);
		}

	}

}
