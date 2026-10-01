package com.example.adrmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AdrManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdrManagerApplication.class, args);
	}

}
