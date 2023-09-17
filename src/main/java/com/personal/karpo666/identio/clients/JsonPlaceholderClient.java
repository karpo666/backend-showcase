package com.personal.karpo666.identio.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.karpo666.identio.models.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Class containing JsonPlaceholder client implementation.
 */
@ApplicationScoped
@Slf4j
public class JsonPlaceholderClient {

    @ConfigProperty(name = "json-placeholder.url.base", defaultValue = "https://jsonplaceholder.typicode.com")
    private String baseUrl;

    @ConfigProperty(name = "json-placeholder.url.users", defaultValue = "/users")
    private String usersPath;

    @ConfigProperty(name = "json-placeholder.url.posts", defaultValue = "/posts")
    private String postsPath;

    @Inject
    ObjectMapper mapper;

    /**
     * Get all users.
     * @return a list of {@link User}.
     * @throws JsonPlaceHolderRestException if rest operation fails or returns something other than 200-OK.
     * @throws JsonProcessingException if Jackson fails to read received json-string.
     */
    public List<User> getUsers() throws JsonPlaceHolderRestException, JsonProcessingException {
        log.debug("Fetching all users.");

        // Construct url.
        URI uri = URI.create(baseUrl + usersPath);

        // Build request.
        HttpRequest request = HttpRequest.newBuilder()
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .uri(uri)
            .GET()
            .build()
        ;

        HttpResponse<String> response = null;

        // Attempt to send request.
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        } catch(InterruptedException e) {
            log.error("Thread was interrupted when sending a request for all users.");
            Thread.currentThread().interrupt();
        } catch(IOException e) {
            log.error("Exception occurred when sending a request for all users");
        }

        // Handle possible nulls and status codes other than 200-OK.
        if (response == null) {
            throw new JsonPlaceHolderRestException(
                "No response was received from JsonPlaceHolder when requesting all users.",
                500
            );
        }

        if (response.statusCode() != 200) {
            throw new JsonPlaceHolderRestException(
                String.format("JsonPlaceholder responded with status code %s when fetching all users", response.statusCode()),
                response.statusCode()
            );
        }

        if (response.body() == null || response.body().isEmpty()) {
            throw new JsonPlaceHolderRestException(
                String.format("JsonPlaceholder responded with status code %s and a null or empty body when fetching all users", response.statusCode()),
                response.statusCode()
            );
        }

        return mapper.readValue(response.body(), new TypeReference<>() {});
    }

    /**
     * Get user with given id.
     * @param userId id with which we do the query.
     * @return {@link User}.
     * @throws JsonPlaceHolderRestException if rest operation fails or returns something other than 200-OK.
     * @throws JsonProcessingException if Jackson fails to read received json-string.
     */
    public User getUser(String userId) throws JsonPlaceHolderRestException, JsonProcessingException {
        log.debug("Fetching user with id: {}.", userId);

        // Construct url.
        URI uri = URI.create(baseUrl + usersPath + "/" + userId);

        // Build request.
        HttpRequest request = HttpRequest.newBuilder()
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .uri(uri)
            .GET()
            .build()
        ;

        HttpResponse<String> response = null;

        // Attempt to send request.
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        } catch(InterruptedException e) {
            log.error("Thread was interrupted when fetching user with id: {}.", userId);
            Thread.currentThread().interrupt();
        } catch(IOException e) {
            log.error("Exception occurred when fetching user with id: {}.", userId);
        }

        // Handle possible nulls and status codes other than 200-OK.
        if (response == null) {
            throw new JsonPlaceHolderRestException(
                String.format("No response was received from JsonPlaceHolder when fetching user with id: %s.", userId),
                500
            );
        }

        if (response.statusCode() != 200) {
            throw new JsonPlaceHolderRestException(
                String.format(
                    "JsonPlaceholder responded with status code %s when fetching user with id: %s",
                    response.statusCode(),
                    userId
                ),
                response.statusCode()
            );
        }

        if (response.body() == null || response.body().isEmpty()) {
            throw new JsonPlaceHolderRestException(
                String.format(
                    "JsonPlaceholder responded with status code %s and a null or empty body when fetching user with id: %s",
                    response.statusCode(),
                    userId
                ),
                response.statusCode()
            );
        }

        return mapper.readValue(response.body(), User.class);
    }

    /**
     * Exception specifically thrown by rest operations to/with JsonPlaceholder.
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public static class JsonPlaceHolderRestException extends Exception {

        private final int statusCode;

        public JsonPlaceHolderRestException(String message, int statusCode) {
            super(message);
            this.statusCode = statusCode;
        }
    }
}
