package com.personal.karpo666.showcase.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.personal.karpo666.showcase.clients.JsonPlaceholderClient;
import com.personal.karpo666.showcase.models.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@Slf4j
public class UsersService {

    @Inject
    JsonPlaceholderClient client;

    // JsonPlaceholder has 10 users saved. This will not change ever.
    // Used to generate user ids for new users.
    private static final int JP_USERS = 10;

    public List<User> getAllUsers() throws JsonPlaceholderClient.JsonPlaceHolderRestException, JsonProcessingException {
        log.info("Getting all users.");

        // Fetching all users from mongodb.
        List<User> mongoUsers = User.listAll();
        List<String> mongoIds = mongoUsers.stream().map(User::getUserId).toList();

        // Fetching users from JsonPlaceholder and removing users found in mongodb from the result.
        List<User> users = client.getUsers().stream().filter(user -> !mongoIds.contains(user.getUserId())).toList();

        // Combining users.
        mongoUsers.addAll(users);
        return mongoUsers;
    }

    public User getUser(String userId) throws JsonPlaceholderClient.JsonPlaceHolderRestException, JsonProcessingException {
        log.info("Getting user with id: {}", userId);

        // Query mongodb for given userId.
        Optional<User> userOptional = User.findByUserId(userId);

        // If user is found, return.
        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        // Query user from JsonPlaceholder.
        return client.getUser(userId);
    }

    public User createNewUser(User user) {
        log.info("Creating new user.");

        // Generating new userId based on the number of existing users.
        String id = String.valueOf(User.count() + JP_USERS + 1);

        // Add user info to mongodb.
        user.setUserId(id);
        user.persistOrUpdate();

        return user;
    }

    public void updateExistingUser(User user) throws JsonPlaceholderClient.JsonPlaceHolderRestException, JsonProcessingException {
        log.info("Updating user");

        // Check that a user with id exists.
        // Propagate exception if one occurs.
        var existingUser = getUser(user.getUserId());

        user.setId(existingUser.id);
        user.persistOrUpdate();
    }
}