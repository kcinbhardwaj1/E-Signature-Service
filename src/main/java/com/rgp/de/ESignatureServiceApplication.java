package com.rgp.de;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication // This annotation boostraps and auto-configure the application.
@EnableCircuitBreaker // This annotation enables the circuit breaker for the microservice.
@EnableHystrixDashboard
@EnableSwagger2
@EnableCaching
public class ESignatureServiceApplication{
	
	public static void main(String[] args) {
		SpringApplication.run(ESignatureServiceApplication.class, args);
	}

}
