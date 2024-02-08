package com.tech.gameService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient // This annotation is used to make this userService as a client of Eureka Server
@SpringBootApplication
public class GameServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GameServiceApplication.class, args);
	}

}