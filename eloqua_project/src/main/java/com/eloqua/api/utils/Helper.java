package com.eloqua.api.utils;

import java.util.Map;

public class Helper {

    /**
     * Converts body of POST/PUT request from HashMap into String.
     *
     * @return String representation of POST/PUT request body.
     */
    public static String createPostRequestBody(final Map<String,String> map) {
        String body = "";
        for (Map.Entry<String, String> entry : map.entrySet()) {
            body += ",\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"";
        }
        return body.replaceFirst(",", "{") + "}";
    }
}
