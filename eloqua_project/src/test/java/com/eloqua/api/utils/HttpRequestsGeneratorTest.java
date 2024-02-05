package com.eloqua.api.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestsGeneratorTest {

    private String fullContactsUrl;
    private String singleContactUrl;
    private Map<String, String> requestBody;

    @BeforeEach
    void init() {
        fullContactsUrl = "https://secure.p02.eloqua.com/API/REST/2.0/data/contacts";
        singleContactUrl = "https://secure.p02.eloqua.com/API/REST/2.0/data/contact";
        requestBody = new HashMap<>();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/properties.csv", numLinesToSkip = 1)
    void testRequestToGetContactsWithoutParameters(final String token) {
        URI expected = URI.create("https://secure.p02.eloqua.com/API/REST/2.0/data/contacts");
        URI actual = HttpRequestsGenerator.generateGetRequest(URI.create(fullContactsUrl), token).uri();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/properties.csv", numLinesToSkip = 1)
    void testRequestToGetContactsWIthMaxNumberOfEntries(final String token) {
        final int maxRows = 10;
        URI expected = URI.create("https://secure.p02.eloqua.com/API/REST/2.0/data/contacts?count=10");
        URI actual = HttpRequestsGenerator.generateGetRequest(URI.create(fullContactsUrl), token, maxRows).uri();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/properties.csv", numLinesToSkip = 1)
    void testRequestToGetContactsWithFilter(final String token) {
        final String term = "id";
        final String operator = "=";
        final String value = "6153467";
        URI expected = URI.create("https://secure.p02.eloqua.com/API/REST/2.0/data/contacts?search=id=6153467");
        URI actual = HttpRequestsGenerator.generateGetRequest(URI.create(fullContactsUrl), token, term, operator, value).uri();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/properties.csv", numLinesToSkip = 1)
    void testRequestToGetSingleContact(final String token) {
        final String id = "6153465";
        URI expected = URI.create("https://secure.p02.eloqua.com/API/REST/2.0/data/contact/6153465");
        URI actual = HttpRequestsGenerator.generateGetRequest(URI.create(singleContactUrl), token, id).uri();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/properties.csv", numLinesToSkip = 1)
    void testRequestToCreateContact(final String token) {
        if (!requestBody.isEmpty()) requestBody.clear();
        requestBody.put("emailAddress", "testingemail1140@gmail.com");
        URI expected = URI.create("https://secure.p02.eloqua.com/API/REST/2.0/data/contact");
        URI actual = HttpRequestsGenerator.generatePostRequest(URI.create(singleContactUrl), token, requestBody).uri();
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/properties.csv", numLinesToSkip = 1)
    void testRequestToUpdateContact(final String token) {
        if (!requestBody.isEmpty()) requestBody.clear();
        requestBody.put("emailAddress", "jps+12@gmail.com");
        requestBody.put("id", "6153465");
        requestBody.put("businessPhone", "555-555-5556");
        final String id = "6153465";
        URI expected = URI.create("https://secure.p02.eloqua.com/API/REST/2.0/data/contact/6153465");
        URI actual = HttpRequestsGenerator.generatePutRequest(URI.create(singleContactUrl), token, id, requestBody).uri();
        assertEquals(expected, actual);
    }
}