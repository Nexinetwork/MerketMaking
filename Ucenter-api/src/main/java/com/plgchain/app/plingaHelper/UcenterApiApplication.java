package com.plgchain.app.plingaHelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@ComponentScan(basePackages={"com.plgchain.app"})
public class UcenterApiApplication {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(UcenterApiApplication.class, args);

	}



}
