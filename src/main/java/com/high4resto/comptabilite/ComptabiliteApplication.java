package com.high4resto.comptabilite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ComptabiliteApplication implements CommandLineRunner {
	private final Logger logger = LoggerFactory.getLogger(ComptabiliteApplication.class);;

	public static void main(String[] args) {
		SpringApplication.run(ComptabiliteApplication.class, args);
	}

	// open ip adrr show eth0
	private String getIp() {
		String ip = "";
		try {
			Process p = Runtime.getRuntime().exec("ip addr show eth0");
			p.waitFor();
			java.io.BufferedReader reader = new java.io.BufferedReader(
					new java.io.InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = reader.readLine()) != null) {
				if (line.contains("inet ")) {
					ip = line.split("inet ")[1].split("/")[0];
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ip;
	}

	@Override
	public void run(String... args) throws Exception {
		logger.info("Bienvenu sur votre logiciel de comptabilité");
		logger.info("Pour accéder à l'interface: http://" + getIp() + ":8080" + "/index.xhtml");
	}
}
