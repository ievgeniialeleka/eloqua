package com.eloqua.api.requests;

import com.eloqua.api.entity.Contact;
import com.eloqua.api.entity.Element;
import com.eloqua.api.utils.Helper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static com.eloqua.api.utils.ProjectLogger.logger;
import static java.net.http.HttpClient.newHttpClient;

/**
 * Used for sending GET, POST and PUT requests to Contacts endpoint in Eloqua, consuming and processing responses.
 */
public class ContactsApi {

    /**
     * String representation of the hardcoded part of the Uri to access contacts endpoint in Eloqua
     */
    private static final String CONTACTS_URL = "2.0/data/contacts";

    /**
     * String representation of the hardcoded part of the Uri to access a single contact endpoint in Eloqua
     */
    private static final String SINGLE_CONTACT_URL = "2.0/data/contact";

    /**
     * URI to access contacts endpoint in Eloqua
     */
    private URI fullContactsUri;

    /**
     * URI to access single contact endpoint in Eloqua
     */
    private URI singleContactUri;
    private String authToken;

    /**
     * Constructor
     *
     * @param basesUriString String representation of a base URL received in response to Authentication request
     * @param authToken String representation of authorization token required for creating any requests to Eloqua
     */
    public ContactsApi(@NotNull final String basesUriString,
                       @NotNull final String authToken) {
        this.fullContactsUri = URI.create(basesUriString + CONTACTS_URL);
        this.singleContactUri = URI.create(basesUriString + SINGLE_CONTACT_URL);
        this.authToken = authToken;
    }

    /**
     * Generates and sends GET HttpRequest to Contacts endpoint in Eloqua.
     * Contains Authorization header.
     * Consumes and processes the response.
     *
     * @return List of the Contacts received from Eloqua in response to GET request.
     */
    public List<Contact> getEloquaContacts() {
        HttpClient client = newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(this.fullContactsUri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + this.authToken)
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            logger.info(e.getMessage());
        }

        return parseResponseMultipleRecords(response.body());
    }

    /**
     * Generates and sends GET HttpRequest to Contacts endpoint in Eloqua.
     * Contains Authorization header and maximum numbers of entries to return.
     * Consumes and processes the response.
     *
     * @return List of the Contacts received from Eloqua in response to GET request.
     */
    public List<Contact> getEloquaContacts(final int maxRows) {
        HttpClient client = newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(this.fullContactsUri + "?count=" + maxRows))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + this.authToken)
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            logger.info(e.getMessage());
        }

        return parseResponseMultipleRecords(response.body());
    }


    /**
     * Generates and sends GET HttpRequest to Contacts endpoint in Eloqua.
     * Contains Authorization header and search parameter which specifies the search criteria to use to filter the results.
     * The syntax for the search parameter is:
     * search={term}{operator}{value}
     * Consumes and processes the response.
     *
     * @return List of the Contacts received from Eloqua in response to GET request.
     */
    public List<Contact> getEloquaContacts(final String term, final String operator, final String value) {
        final String filter = term + operator + value;
        HttpClient client = newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(this.fullContactsUri + "?search=" + filter))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + this.authToken)
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            logger.info(e.getMessage());
        }

        return parseResponseMultipleRecords(response.body());
    }


    /**
     * Generates and sends GET HttpRequest to Contacts endpoint in Eloqua to retrieve a single item.
     * Contains Authorization header and ID of the record to be retrieved.
     * Consumes and processes the response.
     *
     * @return Contact object received from Eloqua in response to GET request.
     */
    public Contact getSingleContact(final String id) {
        HttpClient client = newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(this.singleContactUri + "/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + this.authToken)
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            logger.info(e.getMessage());
        }

        return parseResponseSingleRecord(response.body());
    }

    /**
     * Generates and sends POST HttpRequest to Contacts endpoint in Eloqua to create a new Contact record.
     * Contains Authorization header and body of the request.
     * Consumes and processes the response.
     *
     * @return String representation of request status and id of a newly created record.
     */
    public String createEloquaContact(final Map<String,String> map) {
        HttpClient client = newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(this.singleContactUri)
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + this.authToken)
                .POST(HttpRequest.BodyPublishers.ofString(Helper.createPostRequestBody(map)))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            logger.info(e.getMessage());
        }
        return String.format("Status: %d\nID created: %s", response.statusCode(), parseResponseSingleRecord(response.body()).getId());
    }

    /**
     * Generates and sends PUT HttpRequest to Contacts endpoint in Eloqua to update an existing Contact record.
     * Contains Authorization header and body of the request.
     * Consumes and processes the response.
     *
     * @return Contact object updated by PUT request.
     */
    public Contact updateEloquaContact(final Map<String,String> map, final String id) {
        HttpClient client = newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri((URI.create(this.singleContactUri + "/" + id)))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + this.authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(Helper.createPostRequestBody(map)))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            logger.info(e.getMessage());
        }
        return parseResponseSingleRecord(response.body());
    }

    /**
     * Converts Json response received from Eloqua into the list of Contact objects.
     *
     * @return List<Contact>
     */
    public List<Contact> parseResponseMultipleRecords(String jsonString) {
        Element element = null;
        try {
            element = new ObjectMapper().readValue(jsonString, Element.class);
        } catch (JsonProcessingException e) {
            logger.info(e.getMessage());
        }
        return element.getElements();
    }

    /**
     * Converts Json response received from Eloqua into a single Contact object.
     *
     * @return Contact
     */
    public Contact parseResponseSingleRecord(String jsonString) {
        Contact contact = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            contact = objectMapper.readValue(jsonString, Contact.class);
        } catch (JsonProcessingException e) {
            logger.info(e.getMessage());
        }
        return contact;
    }
}
