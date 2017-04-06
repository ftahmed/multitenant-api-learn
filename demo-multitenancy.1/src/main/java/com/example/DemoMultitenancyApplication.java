package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableConfigurationProperties
public class DemoMultitenancyApplication {

	private static final Logger log = LoggerFactory.getLogger(DemoMultitenancyApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(DemoMultitenancyApplication.class, args);
	}
}
