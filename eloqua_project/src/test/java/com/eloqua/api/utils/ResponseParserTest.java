package com.eloqua.api.utils;

import com.eloqua.api.entity.Contact;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ResponseParserTest {
    @Test
    void testParseResponseMultipleRecords() throws JsonProcessingException {
        final String jsonString = "{\"elements\":[{\"type\":\"Contact\",\"id\":\"6153465\",\"createdAt\":\"1431095317\",\"depth\":\"minimal\"," +
                "\"name\":\"jps+12@gmail.com\",\"updatedAt\":\"1706820169\"},{\"type\":\"Contact\",\"id\":\"6153466\",\"createdAt\":\"1431095317\"," +
                "\"depth\":\"minimal\",\"name\":\"jps+13@gmail.com\",\"updatedAt\":\"1693247713\"},{\"type\":\"Contact\",\"id\":\"6153467\"," +
                "\"createdAt\":\"1431095317\",\"depth\":\"minimal\",\"name\":\"jps+14@gmail.com\",\"updatedAt\":\"1693247714\"}," +
                "{\"type\":\"Contact\",\"id\":\"6153468\",\"createdAt\":\"1431095317\",\"depth\":\"minimal\",\"name\":\"jps+15@gmail.com\"," +
                "\"updatedAt\":\"1631820525\"},{\"type\":\"Contact\",\"id\":\"6153469\",\"createdAt\":\"1431095317\",\"depth\":\"minimal\"," +
                "\"name\":\"jps+16@gmail.com\",\"updatedAt\":\"1600112797\"}],\"page\":1,\"pageSize\":5,\"total\":28554}";
        List<Contact> contactsList = ResponseParser.parseResponseMultipleRecords(jsonString);
        assertNotNull(contactsList);
        assertTrue(contactsList.stream()
                .map(Contact::getType)
                .allMatch(t -> t.equals("Contact")));

        assertTrue(contactsList.stream()
                .map(Contact::getName)
                .allMatch(n -> n.contains("@")));
        assertTrue(contactsList.size() == 5);
    }

    @Test
    void testParseResponseMultipleRecordsException() {
        final String jsonString = "{\"elements\":[{\"type\":\"Contact\",\"id\":\"6153465\",\"createdAt\":\"1431095317\",\"depth\":\"minimal\"," +
                "\"name\":\"jps+12@gmail.com\",\"updatedAt\"}";
        assertThrows(JsonProcessingException.class, () -> ResponseParser.parseResponseMultipleRecords(jsonString));
    }

    @Test
    void testParseResponseSingleRecord() throws JsonProcessingException {
        final String jsonString = "{\"type\":\"Contact\",\"currentStatus\":\"Awaiting action\",\"id\":\"6153465\",\"createdAt\":\"1431095317\"," +
                "\"depth\":\"complete\",\"name\":\"jps+12@gmail.com\",\"updatedAt\":\"1706824569\",\"businessPhone\":\"555-555-5555\"," +
                "\"emailAddress\":\"jps+12@gmail.com\",\"emailFormatPreference\":\"unspecified\"}";
        Contact contact = ResponseParser.parseResponseSingleRecord(jsonString);
        final String id = "6153465";
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

    @Test
    void testParseResponseSingleRecordException() {
        final String jsonString = "[{\"type\":\"Contact\",\"currentStatus\":\"Awaiting action\",\"id\":\"6153465\",\"createdAt\":\"1431095317\"," +
                "\"depth\":\"complete\",\"name\":\"jps+12@gmail.com\"}";
        assertThrows(JsonProcessingException.class, () -> ResponseParser.parseResponseSingleRecord(jsonString));
    }
}