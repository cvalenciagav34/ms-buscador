package com.unir.buscadorApp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class BuscadorApplication {

	public static void main(String[] args) {
		// Retrieve execution profile from environment variable. If not present, default profile is selected.
		String profile = System.getenv("PROFILE");
		System.setProperty("spring.profiles.active", profile != null ? profile : "default");
		// Railway's internal interface takes some time to start. We wait for it to be ready.
		log.debug("Waiting fo Internal Interface to start...");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		SpringApplication.run(BuscadorApplication.class, args);
	}
}
