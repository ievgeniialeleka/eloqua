package com.eloqua.api.requests;

import com.eloqua.api.entity.Contact;
import com.eloqua.api.utils.Authorization;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ContactsApiTest {

    private static final String COMPANY_NAME = System.getenv("COMPANY");
    private static final URI LOGIN_URI = URI.create(System.getenv("URI"));
    private static final String USER_NAME = System.getenv("USER");
    private static final String PASSWORD = System.getenv("PWD");

    private Authorization authorization;
    private ContactsApi contacts;
    private String id;
    private Map<String, String> requestBody;

    @BeforeEach
    void init() throws JSONException {
        authorization = new Authorization(COMPANY_NAME, LOGIN_URI, USER_NAME, PASSWORD);
        authorization.authorize();
        String baseUriString = authorization.getBaseUrl();
        String authToken = authorization.getAuthToken();
        contacts = new ContactsApi(baseUriString, authToken);
        id = "6153465";
        requestBody = new HashMap<>();
    }

    @Test
    void testGetEloquaContactWithoutParameters() throws JsonProcessingException {
        List<Contact> contactsList = contacts.getEloquaContacts();
        assertNotNull(contactsList);
        assertTrue(contactsList.stream()
                .map(Contact::getType)
                .allMatch(t -> t.equals("Contact")));

        assertTrue(contactsList.stream()
                .map(Contact::getName)
                .allMatch(n -> n.contains("@")));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 100, 1000})
    void testGetEloquaContactMaxNumbersOfEntries(int number) throws JsonProcessingException {
        List<Contact> contactsList = contacts.getEloquaContacts(number);
        assertNotNull(contactsList);
        assertTrue(contactsList.stream()
                .map(Contact::getType)
                .allMatch(t -> t.equals("Contact")));

        assertTrue(contactsList.stream()
                .map(Contact::getName)
                .allMatch(n -> n.contains("@")));
        assertTrue(contactsList.size() == number);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -5, 1001})
    void testGetEloquaContactMaxNumbersOfEntriesExceptions(int number) {
        assertThrows(MismatchedInputException.class, () -> contacts.getEloquaContacts(number));
    }

    @ParameterizedTest
    @CsvSource({
            "id, =, 6153467",
            "id, =, 6153465"
    })
    void testGetEloquaContactWithFilter(String term, String operator, String value) throws JsonProcessingException {
        List<Contact> contactsList = contacts.getEloquaContacts(term, operator, value);
        assertNotNull(contactsList);
        assertTrue(contactsList.stream()
                .map(Contact::getType)
                .allMatch(t -> t.equals("Contact")));

        assertTrue(contactsList.stream()
                .map(Contact::getName)
                .allMatch(n -> n.contains("@")));
        assertTrue(contactsList.stream()
                .map(Contact::getId)
                .allMatch(n -> n.equals(value)));
    }

    @Test
    void testGetSingleContact() throws JsonProcessingException {
        Contact contact = contacts.getSingleContact(id);
        assertNotNull(contact);
        String expectedId = id;
        String actualId = contact.getId();
        assertEquals(expectedId, actualId);
        String expectedName = "jps+12@gmail.com";
        String actualName = contact.getName();
        assertEquals(expectedName, actualName);
        String expectedEmail = "jps+12@gmail.com";
        String actualEmail = contact.getEmailAddress();
        assertEquals(expectedEmail, actualEmail);
    }

    @ParameterizedTest
    @CsvSource({
            "emailAddress, testpif1234@gmail.com",
            "emailAddress, testingemail24@gmail.com"
    })
    void testCreateEloquaContact(String key, String value) throws JsonProcessingException {
        if (!requestBody.isEmpty()) requestBody.clear();
        requestBody.put(key, value);
        String actualOutput = contacts.createEloquaContact(requestBody);
        String expectedStatus = "Status: 201\n";
        String actualStatus = actualOutput.substring(0, actualOutput.indexOf("I"));
        assertEquals(expectedStatus, actualStatus);

        String createdId = actualOutput.substring(actualOutput.lastIndexOf(':') + 1).trim();
        assertNotNull(createdId);
        Contact contact = contacts.getSingleContact(createdId);
        assertNotNull(contact);
        assertEquals(value, contact.getEmailAddress());
    }

    @Test
    void testUpdateEloquaContact() throws JsonProcessingException {
        if (!requestBody.isEmpty()) requestBody.clear();
        requestBody.put("emailAddress", "jps+12@gmail.com");
        requestBody.put("id", "6153465");
        requestBody.put("businessPhone", "555-555-5556");
        Contact contact = contacts.updateEloquaContact(requestBody, id);
        String expectedBusinessPhone = "555-555-5556";
        String actualBusinessPhone = contact.getBusinessPhone();
        assertEquals(expectedBusinessPhone, actualBusinessPhone);
    }

    @AfterEach
    void tearDown() {
        authorization = null;
        contacts = null;
        requestBody = null;
    }
}
