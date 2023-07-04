package com.plgchain.app.plingaHelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

@EnableScheduling
@SpringBootApplication
@EnableDiscoveryClient
@EnableAsync
@ComponentScan(basePackages={"com.plgchain.app"})
//@EnableSchedulerLock(defaultLockAtMostFor = "PT300S")
public class BlockchainHealthBusinessApplication {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(BlockchainHealthBusinessApplication.class, args);

	}



}
