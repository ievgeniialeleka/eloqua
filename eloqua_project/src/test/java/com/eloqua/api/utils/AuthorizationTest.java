package com.eloqua.api.utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import java.net.URI;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;

public class AuthorizationTest {

    private static final String COMPANY_NAME = System.getenv("COMPANY");
    private static final URI LOGIN_URI = URI.create(System.getenv("URI"));
    private static final String USER_NAME = System.getenv("USER");
    private static final String PASSWORD = System.getenv("PWD");

    private static Authorization authorization;

    @BeforeAll
    static void init() {
        authorization = new Authorization(COMPANY_NAME, LOGIN_URI, USER_NAME, PASSWORD);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/properties.csv", numLinesToSkip = 1)
    void testAuthorizationSuccessful(String token) throws Exception {
        String actualToken = authorization.getAuthToken();
        String expectedToken = token;
        assertEquals(expectedToken, actualToken);
        assertTrue(authorization.authorize());
        String actualBaseUrl = authorization.getBaseUrl();
        String expectedBaseUrl = "https://secure.p02.eloqua.com/API/REST/";
        assertEquals(expectedBaseUrl, actualBaseUrl);
        JSONObject actual = authorization.sendRequest();
        assertNotNull(actual);
        assertTrue(actual.getJSONObject("urls")
                .getJSONObject("apis")
                .getJSONObject("rest")
                .getString("standard").contains(".eloqua.com/API/REST/{version}/"));

    }

    @ParameterizedTest
    @MethodSource("mockInvalidOrEmptyResponse")
    void testAuthorizationInvalidOrEmptyResponse(JSONObject response) throws Exception {
        if (authorization.getBaseUrl() != null) {
            authorization.setBaseUrl(null);
        }

        Authorization spyAuthorization = Mockito.spy(authorization);
        doReturn(response).when(spyAuthorization).sendRequest();

        JSONException jsonException = assertThrows(JSONException.class, spyAuthorization::authorize);
        assertEquals("Unsupported authorization response", jsonException.getMessage());
        assertNull(spyAuthorization.getBaseUrl());
    }

    private static Stream<Arguments> mockInvalidOrEmptyResponse() {
        return Stream.of(
                Arguments.of(new JSONObject("{\"invalidResponse\": \"value\"}")),
                Arguments.of(new JSONObject("{\"urls\": {\"apis\": {\"rest\": {\"standard\": \"\"}}}}"))
        );
    }

    @AfterAll
    static void tearDown() {
        authorization = null;
    }
}
