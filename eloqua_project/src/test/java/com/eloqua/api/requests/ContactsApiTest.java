package com.eloqua.api.requests;

import com.eloqua.api.entity.Contact;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import com.eloqua.api.utils.Authorization;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ContactsApiTest {

    private static final String COMPANY_NAME = System.getenv("COMPANY");
    private static final URI LOGIN_URI = URI.create(System.getenv("URI"));
    private static final String USER_NAME = System.getenv("USER");
    private static final String PASSWORD = System.getenv("PWD");

    private static Authorization authorization;
    private static ContactsApi contacts;
    private static String id;
    private static Map<String,String> requestBody;

    @BeforeAll
    static void init() {
        authorization = new Authorization(COMPANY_NAME, LOGIN_URI, USER_NAME, PASSWORD);
        authorization.authorize();
        String baseUriString = authorization.getBaseUrl();
        String authToken = authorization.getAuthToken();
        contacts = new ContactsApi(baseUriString, authToken);
        id = "6153465";
        requestBody = new HashMap<>();
    }

    @Test
    void testGetEloquaContactWithoutParameters() {
        List<Contact> contactsList = contacts.getEloquaContacts();
        assertAll(
                () -> assertNotNull(contactsList),
                () -> assertTrue(contactsList.stream()
                        .map(Contact::getType)
                        .allMatch(t -> t.equals("Contact"))),

                () -> assertTrue(contactsList.stream()
                        .map(Contact::getName)
                        .allMatch(n -> n.contains("@")))
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 10, 15})
    void testGetEloquaContactMaxNumbersOfEntries(int number) {
        List<Contact> contactsList = contacts.getEloquaContacts(number);
        assertAll(
                () -> assertNotNull(contactsList),
                () -> assertTrue(contactsList.stream()
                        .map(Contact::getType)
                        .allMatch(t -> t.equals("Contact"))),

                () -> assertTrue(contactsList.stream()
                        .map(Contact::getName)
                        .allMatch(n -> n.contains("@"))),
                () -> assertTrue(contactsList.size() == number)
        );
    }

    @ParameterizedTest
    @CsvSource({
            "id, =, 6153467",
            "id, =, 6153465"
    })
    void testGetEloquaContactWithFilter(String term, String operator, String value) {
        List<Contact> contactsList = contacts.getEloquaContacts(term, operator, value);
        assertAll(
                () -> assertNotNull(contactsList),
                () -> assertTrue(contactsList.stream()
                        .map(Contact::getType)
                        .allMatch(t -> t.equals("Contact"))),

                () -> assertTrue(contactsList.stream()
                        .map(Contact::getName)
                        .allMatch(n -> n.contains("@"))),
                () -> assertTrue(contactsList.stream()
                        .map(Contact::getId)
                        .allMatch(n -> n.equals(value)))
        );
    }

    @Test
    void testGetSingleContact() {
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
            "emailAddress, testpif123451@gmail.com",
            "emailAddress, testingemail15@gmail.com"
    })
    void testCreateEloquaContact(String key, String value) {
        if(!requestBody.isEmpty()) requestBody.clear();
        requestBody.put(key,value);
        String actualOutput = contacts.createEloquaContact(requestBody);
        String expectedStatus = "Status: 201\n";
        String actualStatus = actualOutput.substring(0, actualOutput.indexOf("I"));
        assertEquals(expectedStatus,actualStatus);

        String createdId = actualOutput.substring(actualOutput.lastIndexOf(':') + 1).trim();
        assertNotNull(createdId);
        Contact contact = contacts.getSingleContact(createdId);
        assertNotNull(contact);
        assertEquals(value, contact.getEmailAddress());
    }

    @Test
    void testUpdateEloquaContact() {
        if(!requestBody.isEmpty()) requestBody.clear();
        requestBody.put("emailAddress","jps+12@gmail.com");
        requestBody.put("id","6153465");
        requestBody.put("businessPhone","555-555-5556");
        Contact contact = contacts.updateEloquaContact(requestBody, id);
        String expectedBusinessPhone = "555-555-5556";
        String actualBusinessPhone = contact.getBusinessPhone();
        assertEquals(expectedBusinessPhone, actualBusinessPhone);
    }

    @AfterAll
    static void tearDown() {
        authorization = null;
        contacts = null;
        requestBody = null;
    }
}
