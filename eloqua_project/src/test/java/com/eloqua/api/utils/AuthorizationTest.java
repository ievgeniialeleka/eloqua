package com.eloqua.api.utils;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void testAuthorization() throws IOException, InterruptedException {
        String actualToken = authorization.getAuthToken();
        String expectedToken = "VGVjaG5vbG9neVBhcnRuZXJQdXRpdEZvcndhcmRcWXVyYS5JdmFub3Y6eDVJVUxyUHE3bw==";
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

    @AfterAll
    static void tearDown() {
        authorization = null;
    }
}
