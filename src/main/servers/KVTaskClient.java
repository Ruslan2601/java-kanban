package main.servers;

import main.exceptions.ResponseStatusCodeError;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final String serverUrl;
    private final String apiToken;
    private HttpResponse<String> response;

    public KVTaskClient(String serverUrl) {
        this.serverUrl = serverUrl;
        apiToken = getApiToken(serverUrl);
    }

    private String getApiToken(String serverUrl) {
        URI url = URI.create(serverUrl + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .header("Accept", "application/json")
                .build();
        return send(request);
    }

    private String send(HttpRequest request) {
        HttpClient client = HttpClient.newHttpClient();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ResponseStatusCodeError("Что-то пошло не так, код - " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new ResponseStatusCodeError("Что-то пошло не так, код - " + response.statusCode());
        }
        return response.body();
    }

    public void put(String key, String json) {
        URI uri = URI.create(serverUrl + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        send(request);
    }

    public String load(String key) {
        URI uri = URI.create(serverUrl + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        return send(request);
    }
}
