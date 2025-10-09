package com.example.eurekapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class EurekaPiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaPiApplication.class, args);
	}

}
