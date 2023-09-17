package com.personal.karpo666.identio.services;

import com.personal.karpo666.identio.FakeFactory;
import com.personal.karpo666.identio.TestContainerTestResource;
import com.personal.karpo666.identio.clients.JsonPlaceholderClient;
import com.personal.karpo666.identio.models.User;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
@QuarkusTestResource(value = TestContainerTestResource.class, restrictToAnnotatedClass = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTests {

    @Inject
    UsersService usersService;

    @InjectMock
    JsonPlaceholderClient client;

    @Test
    @Order(1)
    void testCreateNewUser() {
        User user = FakeFactory.newUser();
        var updatedUser = usersService.createNewUser(user);

        assertNotNull(updatedUser);
        assertEquals("11", updatedUser.getUserId());
        user.setUserId(updatedUser.getUserId());
        assertEquals(user, updatedUser);
    }

    @Test
    @Order(2)
    void testGetUser() throws Exception {
        final String userId = "11";
        final User user = usersService.getUser(userId);

        assertNotNull(user);

        verify(client, times(0)).getUser(userId);
    }

    @Test
    @Order(3)
    void testGetUserFallbackToClient() throws Exception {
        final String userId = "1000";
        when(client.getUser(any(String.class))).thenReturn(FakeFactory.newUser());

        final User user = usersService.getUser(userId);
        assertNotNull(user);

        verify(client, times(1)).getUser(userId);
    }

    @Test
    @Order(4)
    void testUpdateUser() throws Exception {
        final String userId = "11";
        final String newUsername = "COOL_MAN_77";
        var user = FakeFactory.newUser(userId);
        user.setUsername(newUsername);

        user.setId(usersService.getUser(userId).id);

        usersService.updateExistingUser(user);

        final User updatedUser = usersService.getUser(userId);
        assertNotNull(updatedUser);
        assertEquals(newUsername, updatedUser.getUsername());
        assertAll("Verifying that no other changes were made.",
            () -> assertEquals(user.getEmail(), updatedUser.getEmail()),
            () -> assertEquals(user.getWebsite(), updatedUser.getWebsite())
        );

        verify(client, times(0)).getUser(userId);
    }

    @Test
    @Order(5)
    void testGetAllUsers() throws Exception {
        int usersInJp = 10;
        when(client.getUsers()).thenReturn(Collections.nCopies(usersInJp, FakeFactory.newUser()));
        List<User> allUsers = usersService.getAllUsers();

        assertNotNull(allUsers);
        assertEquals(usersInJp + User.listAll().size(), allUsers.size());

        verify(client, times(1)).getUsers();
    }
}
