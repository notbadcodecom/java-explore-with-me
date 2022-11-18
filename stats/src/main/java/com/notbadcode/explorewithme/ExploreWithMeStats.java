package com.notbadcode.explorewithme;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@OpenAPIDefinition(info = @Info(title = "EWM Stat service API definition",
				version = "1.0",
				description = "ExploreWithMe statistics service"))
@SpringBootApplication
public class ExploreWithMeStats {

	public static void main(String[] args) {
		SpringApplication.run(ExploreWithMeStats.class, args);
	}
}
