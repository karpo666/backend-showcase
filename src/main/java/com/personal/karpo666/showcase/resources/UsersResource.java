package com.personal.karpo666.showcase.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.personal.karpo666.showcase.clients.JsonPlaceholderClient;
import com.personal.karpo666.showcase.models.User;
import com.personal.karpo666.showcase.services.UsersService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponseSchema;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@RequestScoped
@Tag(name = "Users")
@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Slf4j
public class UsersResource {

    @Inject
    UsersService usersService;

    @GET
    @Path("/users")
    @Operation(
        summary = "Fetch all users.",
        description = "Get all users across JsonPlaceholder and mongodb."
    )
    @APIResponseSchema(value = User[].class, responseCode = "200", responseDescription = "Successfully fetched all users as an array.")
    @APIResponse(responseCode = "404", description = "Users not found.")
    @APIResponse(responseCode = "500", description = "Exception occurred when fetching users.")
    public Response getAllUsers() {
        log.debug("Incoming request for all users.");

        // Attempt to get all users.
        // Respond according to exceptions if one occurs.
        try {
            return Response.ok(usersService.getAllUsers()).build();

        } catch (JsonPlaceholderClient.JsonPlaceHolderRestException e) {
            log.error(e.getMessage());
            return Response.status(e.getStatusCode()).build();

        } catch (JsonProcessingException e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/user")
    @Operation(
        summary = "Fetch user with specific id.",
        description = "Search user with given id across JsonPlaceholder and mongodb."
    )
    @APIResponseSchema(value = User.class, responseCode = "200", responseDescription = "Successfully fetched user.")
    @APIResponse(responseCode = "404", description = "User was not found.")
    @APIResponse(responseCode = "400", description = "Bad request. Id might be empty or null.")
    @APIResponse(responseCode = "500", description = "Exception occurred when fetching user.")
    public Response getUser(@Schema(description = "User id. Cannot be null.", required = true) @QueryParam("id") String userId) {
        log.debug("Incoming request for user with id: {}", userId);

        // Validate userId.
        if (userId.isEmpty()) {
            return Response.status(400, "Id cannot be empty.").build();
        }

        // Attempt to get user with given id.
        // Respond according to exceptions if one occurs.
        try {
            return Response.ok().entity(usersService.getUser(userId)).build();

        } catch (JsonPlaceholderClient.JsonPlaceHolderRestException e) {
            log.error(e.getMessage());
            return Response.status(e.getStatusCode()).build();

        } catch (JsonProcessingException e) {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/user")
    @Operation(
        summary = "Create new user.",
        description = "Create new user to mongodb."
    )
    @APIResponseSchema(value = User.class, responseCode = "201", responseDescription = "User saved to mongodb.")
    @APIResponse(responseCode = "400", description = "Bad request. User id might not be empty or null.")
    public Response createNewUser(@Schema(description = "New user info. Id must be left empty.", required = true) User user) {
        log.debug("Incoming request to crate a new user.");

        // Validate user.
        if (user == null || user.getUserId() != null) {
            return Response.status(400, "User id must be left empty.").build();
        }

        // Create new user to mongodb.
        return Response.status(201).entity(usersService.createNewUser(user)).build();
    }

    @PATCH
    @Path("/user")
    @Operation(
        summary = "Update existing user.",
        description = "Update existing user."
    )
    @APIResponse(responseCode = "200", description = "User updated successfully.")
    @APIResponse(responseCode = "404", description = "User was not found.")
    @APIResponse(responseCode = "400", description = "Bad request. Id might be empty or null.")
    @APIResponse(responseCode = "500", description = "Exception occurred when updating user.")
    public Response updateExistingUser(@Schema(description = "Existing user info with changes made to it.", required = true) User user) {
        log.debug("Incoming request to update user.");

        // Validate user.
        if (user == null || user.getUserId() == null || user.getUserId().isEmpty()) {
            return Response.status(400, "User id must not be empty.").build();
        }

        // Attempt to update existing user.
        try {
            usersService.updateExistingUser(user);
            return Response.status(200).build();

        } catch (JsonPlaceholderClient.JsonPlaceHolderRestException e) {
            log.error(e.getMessage());
            return Response.status(e.getStatusCode()).build();

        } catch (JsonProcessingException e) {
            return Response.serverError().build();
        }
    }
}
