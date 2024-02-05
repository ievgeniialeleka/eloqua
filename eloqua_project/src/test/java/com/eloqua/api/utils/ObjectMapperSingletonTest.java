package com.eloqua.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ObjectMapperSingletonTest {
    @Test
    public void testGetInstance() {

        ObjectMapperSingleton instance1 = ObjectMapperSingleton.getInstance();
        assertNotNull(instance1);

        ObjectMapperSingleton instance2 = ObjectMapperSingleton.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    public void testObjectMapper() {
        ObjectMapper objectMapper1 = ObjectMapperSingleton.getInstance().getObjectMapper();
        assertNotNull(objectMapper1);

        ObjectMapper objectMapper2 = ObjectMapperSingleton.getInstance().getObjectMapper();
        assertSame(objectMapper1, objectMapper2);
    }
}