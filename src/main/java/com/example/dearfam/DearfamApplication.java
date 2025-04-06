package com.example.dearfam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class DearfamApplication {

	public static void main(String[] args) {
		SpringApplication.run(DearfamApplication.class, args);
		System.out.println("Dearfam Server start!");
	}

}
