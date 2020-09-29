package com.alexcosta.cursomc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CursomcApplication implements CommandLineRunner{
	
	// Realizando teste provisório de upload antes de criar um endpoint
	// @Autowired
	// private S3Service s3Service;
	
	public static void main(String[] args) {
		SpringApplication.run(CursomcApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Realizando teste provisário de upload antes de criar um endpoint
		// s3Service.uploadFile("C:\\Temp\\fotos\\computador.jpg");
	}
}
