package com.eloqua.api.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

/**
 * Utility class for generating request body content for POST and PUT requests.
 */
public class ResponseBodyGenerator {

    /**
     * Converts a map of key-value pairs into a JSON string for POST/PUT request bodies.
     *
     * @param map The map containing key-value pairs for the request body.
     * @return String representation of the request body content.
     */
    public static String createPostRequestBody(final Map<String,String> map) {
        JSONObject data = new JSONObject();
        JSONArray array = new JSONArray();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            data.put(entry.getKey(), entry.getValue());
        }

        array.put(data);
        String content = array.length() > 0 ? array.get(0).toString() : "";
        return content;
    }
}
