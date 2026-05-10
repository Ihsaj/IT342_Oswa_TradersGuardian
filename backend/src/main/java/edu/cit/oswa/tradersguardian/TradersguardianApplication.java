package edu.cit.oswa.tradersguardian;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TradersguardianApplication {

	public static void main(String[] args) {
		// Load .env file if it exists and set as system properties
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();
		
		// Set environment variables as system properties for Spring to use
		dotenv.entries().forEach(entry -> {
			System.setProperty(entry.getKey(), entry.getValue());
		});
		
		SpringApplication.run(TradersguardianApplication.class, args);
	}

}
