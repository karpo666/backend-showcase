package com.personal.karpo666.showcase.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.Map;
import java.util.Optional;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@MongoEntity(collection = "users")
@NoArgsConstructor
@Schema(description = "Info about a user.")
public class User extends PanacheMongoEntity{

    @Schema(description = "User id. Left empty when creating new user.")
    @JsonProperty("id")
    private String userId;

    @Schema(description = "User's name.")
    private String name;

    @Schema(description = "User's username.")
    private String username;

    @Schema(description = "User's email.")
    private String email;

    @Schema(description = "User's address.")
    private Address address;

    @Schema(description = "User's phone number.")
    private String phone;

    @Schema(description = "User's website/homepage.")
    private String website;

    @Schema(description = "The company at which user is working.")
    private Company company;

    @Schema(description = "Additional info about the user.")
    private AdditionalInfo additionalInfo;

    /**
     * Find user in database and return it wrapped to an optional.
     * @param userId which we query with.
     * @return {@link User} wrapped in {@link Optional}.
     */
    public static Optional<User> findByUserId(String userId) {
        return find("userId", userId).firstResultOptional();
    }

    /**
     * Setter for id used by mongodb.
     * @param id new userId as {@link ObjectId}.
     */
    public void setId(ObjectId id) {
        this.id = id;
    }

    @Data
    @Schema(description = "Info about user's address.")
    public static class Address {

        @Schema(description = "User's home address.")
        private String street;

        @Schema(description = "Suite info.")
        private String suite;

        @Schema(description = "In which city is the user living in.")
        private String city;

        @Schema(description = "Zip code for said city.")
        private String zipCode;

        @Schema(description = "Coordinates for user's home.")
        private Map<String, String> geo;
    }

    @Data
    @Schema(description = "Info about user's workplace.")
    public static class Company {

        @Schema(description = "User's workplace's name.")
        private String name;

        @Schema(description = "User's workplace's catchphrase.")
        private String catchPhrase;

        @Schema(description = "User's workplace's business/area.")
        private String bs;
    }

    @Data
    @Schema(description = "Additional info about the user.")
    public static class AdditionalInfo {

        @Schema(description = "User's favourite color.")
        private String favouriteColor;

        @Schema(description = "User's arch nemesis.")
        private String archEnemy;

        @Schema(description = "How many dogs the user wishes to own one day.")
        private int amountOfDogsTheyHopeToOwnOneDay;

        @Schema(description = "User's greatest fear.")
        private String greatestFear;
    }
}
