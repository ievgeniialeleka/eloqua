package com.eloqua.api.utils;

import com.eloqua.api.entity.Contact;
import com.eloqua.api.entity.Element;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * Utility class for parsing JSON responses received from Eloqua into Contact objects.
 */
@Log4j2
public class ResponseParser {

    /**
     * Converts JSON response received from Eloqua into a list of Contact objects.
     *
     * @param jsonString The JSON response string from Eloqua.
     * @return List of Contact objects parsed from the JSON response.
     * @throws JsonProcessingException If there is an issue processing the JSON.
     */
    public static List<Contact> parseResponseMultipleRecords(final String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = ObjectMapperSingleton.getInstance().getObjectMapper();
        Element element = objectMapper.readValue(jsonString, Element.class);

        return element.getElements();
    }

    /**
     * Converts JSON response received from Eloqua into a single Contact object.
     *
     * @param jsonString The JSON response string from Eloqua.
     * @return Contact object parsed from the JSON response.
     * @throws JsonProcessingException If there is an issue processing the JSON.
     */
    public static Contact parseResponseSingleRecord(final String jsonString) throws JsonProcessingException {
        ObjectMapper objectMapper = ObjectMapperSingleton.getInstance().getObjectMapper();
        Contact contact = objectMapper.readValue(jsonString, Contact.class);

        return contact;
    }
}
