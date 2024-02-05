package com.eloqua.api.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Optional;

import static java.net.http.HttpClient.newHttpClient;

/**
 * Used for authentication and authorization in Eloqua.
 * Base URL for further requests will be received as a part of the response from Eloqua.
 */
@Log4j2
public class Authorization {
    private String siteName;
    private URI loginUri;
    private String userName;
    private String pwd;

    @Getter @Setter
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
     * @return boolean: true if authorization was successful, false if it failed.
     * @throws JSONException if received response is empty or has invalid format.
     */
    public boolean authorize() throws JSONException {

        JSONObject authResponse;
        try {
            authResponse = this.sendRequest();
        } catch (IOException | InterruptedException e) {
            log.info(e.getMessage());
            return false;
        }

        Optional<String> urlResponse = Optional.ofNullable(authResponse)
                .map(json -> json.optJSONObject("urls"))
                .map(urls -> urls.optJSONObject("apis"))
                .map(apis -> apis.optJSONObject("rest"))
                .map(rest -> rest.optString("standard"));

        if(urlResponse.isPresent() && !urlResponse.get().isEmpty()) {
            this.baseUrl = urlResponse.get().substring(0, urlResponse.get().indexOf('{'));
        } else {
            throw new JSONException("Unsupported authorization response");
        }
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
