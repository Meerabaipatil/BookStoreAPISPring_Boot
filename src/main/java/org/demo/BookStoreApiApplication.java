package org.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
@ComponentScan(basePackages = "org.demo")
public class BookStoreApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookStoreApiApplication.class, args);
	}

	 @Bean
	    public RestTemplate restTemplate() {
	        return new RestTemplate();
	    }
}
