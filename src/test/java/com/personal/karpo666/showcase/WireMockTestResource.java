package com.personal.karpo666.showcase;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;

public class WireMockTestResource implements QuarkusTestResourceLifecycleManager {

    WireMockServer wireMockServer;

    public static String TEST_ID = "TEST_ID";

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();

        setupJsonPlaceholderStubs(wireMockServer);

        Map<String, String> testConfigs = new HashMap<>();
        testConfigs.put("json-placeholder.url.base", wireMockServer.baseUrl());
        return testConfigs;
    }

    @Override
    public void stop() {
        if (wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    private void setupJsonPlaceholderStubs(WireMockServer wireMockServer) {
        wireMockServer.stubFor(
            get("/users").inScenario("TEST_GET_USERS")
                .whenScenarioStateIs(STARTED)
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBodyFile("get_all_users_response.json")
                )
                .willSetStateTo("USERS_NOT_FOUND")
        );

        wireMockServer.stubFor(
            get("/users").inScenario("TEST_GET_USERS")
                .whenScenarioStateIs("USERS_NOT_FOUND")
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
                .willReturn(
                    aResponse()
                        .withStatus(404)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody("{}")
                )
                .willSetStateTo("USERS_EMPTY_RESPONSE")
        );

        wireMockServer.stubFor(
            get("/users").inScenario("TEST_GET_USERS")
                .whenScenarioStateIs("USERS_EMPTY_RESPONSE")
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                )
        );

        wireMockServer.stubFor(
            get("/users/" + TEST_ID).inScenario("TEST_GET_USER")
                .whenScenarioStateIs(STARTED)
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBodyFile("get_user_response.json")
                )
                .willSetStateTo("USER_NOT_FOUND")
        );

        wireMockServer.stubFor(
            get("/users/" + TEST_ID).inScenario("TEST_GET_USER")
                .whenScenarioStateIs("USER_NOT_FOUND")
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON))
                .willReturn(
                    aResponse()
                        .withStatus(404)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                        .withBody("{}")
                )
        );
    }
}
