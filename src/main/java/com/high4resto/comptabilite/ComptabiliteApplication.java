package com.high4resto.comptabilite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ComptabiliteApplication implements CommandLineRunner {
	private final Logger logger = LoggerFactory.getLogger(ComptabiliteApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ComptabiliteApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		logger.info("Bienvenu sur mon logiciel de comptabilité");

	}

}
