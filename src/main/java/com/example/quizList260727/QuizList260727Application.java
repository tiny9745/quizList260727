package com.example.quizList260727;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * @SpringBootApplication(exclude = { UserDetailsServiceAutoConfiguration.class,
 *                                ServletWebSecurityAutoConfiguration.class})
 */

@SpringBootApplication
public class QuizList260727Application {
	public static void main(String[] args) {
		SpringApplication.run(QuizList260727Application.class, args);
	}

}
