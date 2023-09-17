package com.personal.karpo666.identio;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.MongoDBContainer;

import java.util.Map;

public class TestContainerTestResource implements QuarkusTestResourceLifecycleManager {

    MongoDBContainer container;
    @Override
    public Map<String, String> start() {
        container = new MongoDBContainer("mongo");
        container.start();

        return Map.of("quarkus.mongodb.connection-string", container.getConnectionString());
    }

    @Override
    public void stop() {
        if (container.isRunning()) {
            container.stop();
        }
    }
}
