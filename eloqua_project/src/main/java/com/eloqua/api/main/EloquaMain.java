package com.eloqua.api.main;

import com.eloqua.api.entity.Contact;
import com.eloqua.api.requests.ContactsApi;
import com.eloqua.api.utils.Authorization;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EloquaMain {
    public static void main(String @NotNull [] args) throws JSONException, JsonProcessingException {

        final String companyName = args[0];
        final URI loginUri = URI.create(args[1]);
        final String userName = args[2];
        final String pwd = args[3];

        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("emailAddress", "jps+12@gmail.com");
        updateMap.put("id", "6153465");
        updateMap.put("businessPhone", "555-555-5555");

        Map<String, String> createMap = new HashMap<>();
        createMap.put("emailAddress", "testingemail149@gmail.com");

        Authorization authorization = new Authorization(companyName, loginUri, userName, pwd);

        boolean isAuthorized = authorization.authorize();

        if (!isAuthorized) { return; }

        ContactsApi contactsApi = new ContactsApi(authorization.getBaseUrl(), authorization.getAuthToken());
        List<Contact> contactsListWithLimit = contactsApi.getEloquaContacts(5);
        for (Contact c : contactsListWithLimit) {
            System.out.println(c);
        }
        System.out.println();

        Contact singleContact = contactsApi.getSingleContact("6153465");
        System.out.println(singleContact);
        System.out.println();

        List<Contact> contactsListWithFilters = contactsApi.getEloquaContacts("id", "=", "6153467");
        for (Contact c : contactsListWithFilters) {
            System.out.println(c);
        }
        System.out.println();

        List<Contact> contactsListMAxRowsLimit = contactsApi.getEloquaContacts(5);
        for (Contact c : contactsListMAxRowsLimit) {
            System.out.println(c);
        }
        System.out.println();

        System.out.println(contactsApi.createEloquaContact(createMap));
        System.out.println();

        Contact updatedContact = contactsApi.updateEloquaContact(updateMap, "6153465");
        System.out.println(updatedContact);
    }
}

