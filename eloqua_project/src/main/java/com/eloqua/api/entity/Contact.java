package com.eloqua.api.entity;

import lombok.Data;

/**
 * Represents Contact POJO to parse the response of the GET contacts request from Eloqua
 */
@Data
public class Contact {

    private String type;
    private String id;
    private String name;
    private String depth;
    private String createdAt;
    private String updatedAt;
    private String currentStatus;
    private String emailAddress;
    private String businessPhone;
}
