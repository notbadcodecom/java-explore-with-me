package com.notbadcode.explorewithme;

import org.testcontainers.containers.PostgreSQLContainer;

public class EwmPostgresqlContainer extends PostgreSQLContainer<EwmPostgresqlContainer> {
    private static final String IMAGE_VERSION = "postgres:13.9";
    private static EwmPostgresqlContainer container;

    private EwmPostgresqlContainer() {
        super(IMAGE_VERSION);
    }

    public static EwmPostgresqlContainer getInstance() {
        if (container == null) {
            container = new EwmPostgresqlContainer();
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
        System.setProperty("DB_URL", container.getJdbcUrl());
        System.setProperty("DB_USERNAME", container.getUsername());
        System.setProperty("DB_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}