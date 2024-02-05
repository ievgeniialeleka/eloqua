package com.eloqua.api.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResponseBodyGeneratorTest {
    private Map<String,String> map;

    @BeforeEach
    void init() {
        map = new HashMap<>();
        map.put("emailAddress","jps+12@gmail.com");
        map.put("id","6153465");
        map.put("businessPhone","555-555-5555");
    }

    @Test
    void createPostRequestBody() {
       String expected = "{\"emailAddress\":\"jps+12@gmail.com\",\"id\":\"6153465\",\"businessPhone\":\"555-555-5555\"}";
       String actual = ResponseBodyGenerator.createPostRequestBody(map);
       assertEquals(expected, actual);

    }

    @AfterEach
    void tearDown() {
        map.clear();
    }
}