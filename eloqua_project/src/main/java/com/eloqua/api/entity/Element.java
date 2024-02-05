package com.eloqua.api.entity;

import lombok.Data;

import java.util.ArrayList;

/**
 * Represents Element POJO to parse the response of the GET contacts request from Eloqua
 */
@Data
public class Element {

    private ArrayList<Contact> elements;
    private int page;
    private int pageSize;
    private int total;
}
