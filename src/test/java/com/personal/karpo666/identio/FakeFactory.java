package com.personal.karpo666.identio;

import com.personal.karpo666.identio.models.User;

import java.util.Map;

public class FakeFactory {

    private FakeFactory() {}

    public static final String NAME = "TEST_NAME";
    public static final String USERNAME = "TEST_USERNAME";
    public static final String EMAIL = "TEST@MAIL.COM";
    public static final String STREET = "TEST_STREET69";
    public static final String SUITE = "TEST_SUITE";
    public static final String CITY = "TESTINKI";
    public static final String ZIP_CODE = "000666";
    public static final Map<String, String> GEO = Map.of("lat", "-0.6", "lng", "6.0");
    public static final String PHONE = "+358TESTING";
    public static final String WEBSITE = "HTTPS://TEST.GOV";
    public static final String COMPANY_NAME = "TESTING OY";
    public static final String CATCH_PHRASE = "LET'S TEST THIS SH*T!! :D";
    public static final String BS = "TESTING";
    public static final String FAVOURITE_COLOR = "BLUE";
    public static final String ARCH_ENEMY = "NEIGHBOUR";
    public static final int AMOUNT_OF_DOGS_THEY_HOPE_TO_OWN_ONE_DAY = 4;
    public static final String GREATEST_FEAR = "TO STOP TESTING";

    public static User newUser() {
        var user = new User();
        user.setName(NAME);
        user.setUsername(USERNAME);
        user.setEmail(EMAIL);
        var address = new User.Address();
        address.setStreet(STREET);
        address.setSuite(SUITE);
        address.setCity(CITY);
        address.setZipCode(ZIP_CODE);
        address.setGeo(GEO);
        user.setAddress(address);
        user.setPhone(PHONE);
        user.setWebsite(WEBSITE);
        var company = new User.Company();
        company.setName(COMPANY_NAME);
        company.setCatchPhrase(CATCH_PHRASE);
        company.setBs(BS);
        return user;
    }

    public static User newUser(String userId) {
        var user = newUser();
        user.setUserId(userId);
        return user;
    }

    public static User newUserWithAdditionalInfo() {
        var user = newUser();
        var additionalInfo = new User.AdditionalInfo();
        additionalInfo.setFavouriteColor(FAVOURITE_COLOR);
        additionalInfo.setArchEnemy(ARCH_ENEMY);
        additionalInfo.setAmountOfDogsTheyHopeToOwnOneDay(AMOUNT_OF_DOGS_THEY_HOPE_TO_OWN_ONE_DAY);
        additionalInfo.setGreatestFear(GREATEST_FEAR);
        user.setAdditionalInfo(additionalInfo);
        return user;
    }

    public static User newUserWithAdditionalInfo(String userId) {
        var user = newUserWithAdditionalInfo();
        user.setUserId(userId);
        return user;
    }
}
