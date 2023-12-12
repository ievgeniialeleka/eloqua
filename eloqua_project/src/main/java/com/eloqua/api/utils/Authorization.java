package com.eloqua.api.utils;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import static java.net.http.HttpClient.newHttpClient;
import static com.eloqua.api.utils.ProjectLogger.logger;

/**
 * Used for authentication and authorization in Eloqua.
 * Base URL for further requests will be received as a part of the response from Eloqua.
 */
public class Authorization {
    private String siteName;
    private URI loginUri;
    private String userName;
    private String pwd;

    @Getter
    private String baseUrl;

    /**
     * Constructor
     *
     * @param siteName company name required for authentication
     * @param loginUri URI to send authentication request
     * @param userName login value required for authentication
     * @param pwd password required for authentication
     */
    public Authorization(@NotNull final String siteName,
                         @NotNull final URI loginUri,
                         @NotNull final String userName,
                         @NotNull final String pwd) {
        this.siteName = siteName;
        this.loginUri = loginUri;
        this.userName = userName;
        this.pwd = pwd;
        this.baseUrl = null;
    }

    /**
     * Evaluates JSONObject received from Eloqua as a response to authorization request and retrieves base URL from it.
     *
     * @return boolean: true if authorization was successful, false if it failed
     */
    public boolean authorize() {

        JSONObject authResponse;
        try {
            authResponse = this.sendRequest();
        } catch (IOException | InterruptedException e) {
            logger.info(e.getMessage());
            return false;
        }
        String urlResponse = authResponse.getJSONObject("urls")
                .getJSONObject("apis")
                .getJSONObject("rest")
                .getString("standard");

        this.baseUrl = urlResponse.substring(0, urlResponse.indexOf('{'));

        return true;
    }


    /**
     * Converts parameters required for authentication in Eloqua into base-64 encoded value.
     *
     * @return String representation of authorization token required for creating any requests to Eloqua
     */
    public String getAuthToken() {
        return Base64.getEncoder().encodeToString(
                (this.siteName + '\\' + this.userName + ':' + this.pwd).getBytes()
        );
    }

    /**
     * Sends HttpRequest for authorization in Eloqua and processes the response converting it to JSONObject.
     *
     * @return Authorization response from Eloqua as a JSONObject
     */
    public JSONObject sendRequest() throws IOException, InterruptedException {
        HttpClient client = newHttpClient();

        final HttpRequest request =  HttpRequest.newBuilder()
                .GET()
                .uri(this.loginUri)
                .header("Authorization", "Basic " + getAuthToken())
                .build();

        HttpResponse<String> authResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        return new JSONObject(authResponse.body());
    }
}
