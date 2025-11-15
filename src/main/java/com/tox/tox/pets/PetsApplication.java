package com.tox.tox.pets;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.tox.tox.pets.mapper")
public class PetsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetsApplication.class, args);
	}

}
