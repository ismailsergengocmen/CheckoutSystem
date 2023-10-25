package com.example.checkoutCase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class CheckoutCaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheckoutCaseApplication.class, args);
	}

}
