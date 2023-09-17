package com.personal.karpo666.identio.clients;

import com.personal.karpo666.identio.WireMockTestResource;
import com.personal.karpo666.identio.models.User;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@QuarkusTestResource(value = WireMockTestResource.class, restrictToAnnotatedClass = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class JsonPlaceHolderClientTests {

    @Inject
    JsonPlaceholderClient client;

    @Test
    @Order(1)
    void testGetUsers() throws Exception {
        List<User> response = client.getUsers();
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    @Order(2)
    void testGetUsersNotFound() {
        JsonPlaceholderClient.JsonPlaceHolderRestException e =
            assertThrows(
                JsonPlaceholderClient.JsonPlaceHolderRestException.class,
                () -> client.getUsers()
            )
        ;

        assertEquals(404, e.getStatusCode());
    }

    @Test
    @Order(3)
    void testGerUsersNoBody() {
        JsonPlaceholderClient.JsonPlaceHolderRestException e =
            assertThrows(
                JsonPlaceholderClient.JsonPlaceHolderRestException.class,
                () -> client.getUsers()
            )
        ;

        assertEquals(200, e.getStatusCode());
    }

    @Test
    @Order(4)
    void testGetUser() throws Exception {
        User response = client.getUser(WireMockTestResource.TEST_ID);
        assertNotNull(response);
    }
}
