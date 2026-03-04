package com.commerzo.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.commerzo.auth.config.SecurityConfiguration;

@SpringBootApplication(scanBasePackages = { "com.commerzo.auth", "com.commerzo.common_config.authentication",
		"com.commerzo.common_config.exception" })
public class AuthServiceApplication {

	private static final Logger log = LoggerFactory.getLogger(AuthServiceApplication.class);

	public static void main(String[] args) {
		log.info(">>> MAIN METHOD STARTED <<<");
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
