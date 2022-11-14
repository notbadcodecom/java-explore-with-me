package ru.practicum.explorewithme;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@OpenAPIDefinition(info = @Info(title = "Explore With Me API definition",
				version = "1.0",
				description = "Service for share events and look for participants for events"))
@SpringBootApplication
public class ExploreWithMeServer {

	public static void main(String[] args) {
		SpringApplication.run(ExploreWithMeServer.class, args);
	}
}
