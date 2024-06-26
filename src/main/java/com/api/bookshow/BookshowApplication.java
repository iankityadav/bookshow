package com.api.bookshow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BookshowApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookshowApplication.class, args);
	}

}
