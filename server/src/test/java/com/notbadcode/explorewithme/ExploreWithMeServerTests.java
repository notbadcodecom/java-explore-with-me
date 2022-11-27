package com.notbadcode.explorewithme;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class ExploreWithMeServerTests {
	@Container
	public static PostgreSQLContainer container = EwmPostgresqlContainer.getInstance();

	@Test
	void contextLoads() {
	}

}
