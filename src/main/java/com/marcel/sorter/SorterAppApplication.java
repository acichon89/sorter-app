package com.marcel.sorter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class SorterAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SorterAppApplication.class, args);
	}
}
