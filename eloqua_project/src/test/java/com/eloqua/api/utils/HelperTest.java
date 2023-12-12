package com.eloqua.api.utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HelperTest {
    private static Map<String,String> map;

    @BeforeAll
    static void init() {
        map = new HashMap<>();
        map.put("emailAddress","jps+12@gmail.com");
        map.put("id","6153465");
        map.put("businessPhone","555-555-5555");
    }

    @Test
    void createPostRequestBody() {
       String expected = "{\"emailAddress\":\"jps+12@gmail.com\",\"id\":\"6153465\",\"businessPhone\":\"555-555-5555\"}";
       String actual = Helper.createPostRequestBody(map);
       assertEquals(expected, actual);

    }

    @AfterAll
    static void tearDown() {
        map.clear();
    }
}