package com.personal.karpo666.showcase.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.karpo666.showcase.FakeFactory;
import com.personal.karpo666.showcase.clients.JsonPlaceholderClient;
import com.personal.karpo666.showcase.models.User;
import com.personal.karpo666.showcase.services.UsersService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
class UsersResourceTests {

    @InjectMock
    UsersService usersService;

    @Inject
    ObjectMapper mapper;

    @Test
    void testGetUsers() throws Exception {
        when(usersService.getAllUsers()).thenReturn(Collections.nCopies(10, FakeFactory.newUser()));
        var resultString =
            given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .get("/api/v1/users")
                .then()
                .statusCode(200)
                .extract().body().asString()
        ;

        assertNotNull(resultString);

        List<User> users = mapper.readValue(resultString, new TypeReference<>() {});
        assertNotNull(users);
        assertEquals(10, users.size());

        verify(usersService, times(1)).getAllUsers();
    }

    @Test
    void testGetUsersNotFound() throws Exception {
        when(usersService.getAllUsers())
            .thenThrow(new JsonPlaceholderClient.JsonPlaceHolderRestException("No users found", 404))
        ;

        given()
            .when()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .get("/api/v1/users")
            .then()
            .statusCode(404)
        ;

        verify(usersService, times(1)).getAllUsers();
    }

    @Test
    void testGetUsersJsonProcessingException() throws Exception {
        when(usersService.getAllUsers()).thenThrow(JsonProcessingException.class);
        given()
            .when()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .get("/api/v1/users")
            .then()
            .statusCode(500)
        ;

        verify(usersService, times(1)).getAllUsers();
    }

    @Test
    void testGetUser() throws Exception {
        final String userId = "TEST_USER_ID";
        when(usersService.getUser(userId)).thenReturn(FakeFactory.newUser(userId));
        var result =
            given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .queryParam("id", userId)
                .get("/api/v1/user")
                .then()
                .statusCode(200)
                .extract().body().as(User.class)
        ;

        assertNotNull(result);
        assertEquals(userId, result.getUserId());

        verify(usersService, times(1)).getUser(any(String.class));
    }

    @Test
    void testGetUserEmptyUserId() {
        given()
            .when()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .queryParam("id", "")
            .get("/api/v1/user")
            .then()
            .statusCode(400)
        ;
    }

    @Test
    void testGetUserNotFound() throws Exception {
        when(usersService.getUser(any(String.class)))
            .thenThrow(new JsonPlaceholderClient.JsonPlaceHolderRestException("User not found", 404))
        ;

        given()
            .when()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .queryParam("id", "TEST")
            .get("/api/v1/user")
            .then()
            .statusCode(404)
        ;

        verify(usersService, times(1)).getUser(any(String.class));
    }

    @Test
    void testGetUserJsonProcessingException() throws Exception {
        when(usersService.getUser(any(String.class))).thenThrow(JsonProcessingException.class)
        ;

        given()
            .when()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .queryParam("id", "TEST")
            .get("/api/v1/user")
            .then()
            .statusCode(500)
        ;

        verify(usersService, times(1)).getUser(any(String.class));
    }

    @Test
    void testCreateUser() {
        final String userId = "TEST_ID";
        when(usersService.createNewUser(any(User.class))).thenReturn(FakeFactory.newUser(userId));

        var result =
            given()
                .when()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .body(FakeFactory.newUser())
                .post("/api/v1/user")
                .then()
                .statusCode(201)
                .extract().body().as(User.class)
        ;

        assertNotNull(result);
        assertEquals(userId, result.getUserId());

        verify(usersService, times(1)).createNewUser(any(User.class));
    }

    @Test
    void testCreateUserUserIdNotNull() {
        given()
            .when()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .body(FakeFactory.newUser("NOT_NULL"))
            .post("/api/v1/user")
            .then()
            .statusCode(400)
        ;
    }

    @Test
    void testCreateUserUserNull() {
        given()
            .when()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .post("/api/v1/user")
            .then()
            .statusCode(400)
        ;
    }

    @Test
    void testUpdateUser() {
        given()
            .when()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .body(FakeFactory.newUser("TEST_ID"))
            .patch("/api/v1/user")
            .then()
            .statusCode(200)
        ;
    }

    @Test
    void testUpdateUserNoUserId() {
        given()
            .when()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .body(FakeFactory.newUser())
            .patch("/api/v1/user")
            .then()
            .statusCode(400)
        ;
    }

    @Test
    void testUpdateUserNullUser() {
        given()
            .when()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .body("")
            .patch("/api/v1/user")
            .then()
            .statusCode(400)
        ;
    }

    @Test
    void testUpdateUserUserNotFound() throws Exception {
        doThrow(new JsonPlaceholderClient.JsonPlaceHolderRestException("User not found.", 404))
            .when(usersService).updateExistingUser(any(User.class))
        ;

        given()
            .when()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .body(FakeFactory.newUser("TEST_ID"))
            .patch("/api/v1/user")
            .then()
            .statusCode(404)
        ;

        verify(usersService, times(1)).updateExistingUser(any(User.class));
    }

    @Test
    void testUpdateUserJsonProcessingException() throws Exception {
        doThrow(JsonProcessingException.class).when(usersService).updateExistingUser(any(User.class));

        given()
            .when()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
            .body(FakeFactory.newUser("TEST_ID"))
            .patch("/api/v1/user")
            .then()
            .statusCode(500)
        ;

        verify(usersService, times(1)).updateExistingUser(any(User.class));
    }
}
