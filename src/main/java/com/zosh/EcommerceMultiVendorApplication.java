package com.zosh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class EcommerceMultiVendorApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceMultiVendorApplication.class, args);
	}

}
