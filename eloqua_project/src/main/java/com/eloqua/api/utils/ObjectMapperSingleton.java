package com.eloqua.api.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Singleton class for managing a shared instance of Jackson's ObjectMapper.
 */
public final class ObjectMapperSingleton {

    /**
     * The singleton instance of ObjectMapperSingleton
     */
    private static volatile ObjectMapperSingleton INSTANCE;

    /**
     * Mutex object for double-checked locking
     */
    private static final Object MUTEX = new Object();
    private final ObjectMapper objectMapper;

    /**
     * Private constructor to prevent external instantiation.
     * Initializes the ObjectMapper and its configuration.
     */
    private ObjectMapperSingleton() {
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Retrieves the singleton instance of ObjectMapperSingleton.
     *
     * @return The singleton instance of ObjectMapperSingleton.
     */
    public static ObjectMapperSingleton getInstance() {
        ObjectMapperSingleton instance = INSTANCE;

        if (instance == null) {
            synchronized (MUTEX) {
                instance = INSTANCE;

                if (instance == null) {
                    INSTANCE = instance = new ObjectMapperSingleton();
                }
            }
        }

        return instance;
    }

    /**
     * Gets the shared instance of ObjectMapper.
     *
     * @return The ObjectMapper instance.
     */
    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }
}
