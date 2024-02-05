package com.eloqua.api.utils;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Map;

/**
 * Utility class for generating various types of HTTP requests with headers for interacting with an API.
 */
public class HttpRequestsGenerator {

    /**
     * Generates a basic GET request with authorization headers.
     *
     * @param uri   The URI for the GET request.
     * @param token The authorization token for the request.
     * @return HttpRequest object representing the generated GET request.
     */
    public static HttpRequest generateGetRequest(final URI uri, final String token) {
        return HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + token)
                .build();
    }

    /**
     * Generates a GET request with a specified maximum number of rows and authorization headers.
     *
     * @param uri     The URI for the GET request.
     * @param token   The authorization token for the request.
     * @param maxRows The maximum number of rows to request.
     * @return HttpRequest object representing the generated GET request.
     */
    public static HttpRequest generateGetRequest(final URI uri, final String token, final int maxRows) {
        return HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "?count=" + maxRows))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + token)
                .build();
    }

    /**
     * Generates a GET request with a search filter and authorization headers.
     *
     * @param uri      The URI for the GET request.
     * @param token    The authorization token for the request.
     * @param term     The search term.
     * @param operator The search operator.
     * @param value    The search value.
     * @return HttpRequest object representing the generated GET request.
     */
    public static HttpRequest generateGetRequest(final URI uri, final String token, final String term, final String operator, final String value) {
        final String filter = term + operator + value;

        return HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "?search=" + filter))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + token)
                .build();
    }

    /**
     * Generates a GET request for a specific resource ID with authorization headers.
     *
     * @param uri   The URI for the GET request.
     * @param token The authorization token for the request.
     * @param id    The resource ID.
     * @return HttpRequest object representing the generated GET request.
     */
    public static HttpRequest generateGetRequest(final URI uri, final String token, final String id) {
        return HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + token)
                .build();
    }

    /**
     * Generates a POST request with authorization headers and a request body.
     *
     * @param uri       The URI for the POST request.
     * @param token     The authorization token for the request.
     * @param valuesMap A map of values for the request body.
     * @return HttpRequest object representing the generated POST request.
     */
    public static HttpRequest generatePostRequest(final URI uri, final String token, final Map<String, String> valuesMap) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + token)
                .POST(HttpRequest.BodyPublishers.ofString(ResponseBodyGenerator.createPostRequestBody(valuesMap)))
                .build();
    }

    /**
     * Generates a PUT request with authorization headers, a resource ID, and a request body.
     *
     * @param uri       The URI for the PUT request.
     * @param token     The authorization token for the request.
     * @param id        The resource ID.
     * @param valuesMap A map of values for the request body.
     * @return HttpRequest object representing the generated PUT request.
     */
    public static HttpRequest generatePutRequest(final URI uri, final String token, final String id, final Map<String, String> valuesMap) {
        return HttpRequest.newBuilder()
                .uri((URI.create(uri + "/" + id)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + token)
                .PUT(HttpRequest.BodyPublishers.ofString(ResponseBodyGenerator.createPostRequestBody(valuesMap)))
                .build();
    }
}
