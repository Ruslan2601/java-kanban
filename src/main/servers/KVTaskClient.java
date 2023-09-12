package main.servers;

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
        URI url = URI.create(serverUrl + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(url)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Проверьте адрес");
        }
        apiToken = response.body();
    }

    public void put(String key, String json) {
        URI uri = URI.create(serverUrl + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Что-то пошло не так, код - " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Проверьте адрес");
        }
    }

    public String load(String key) {
        URI uri = URI.create(serverUrl + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse.BodyHandler<String> s = HttpResponse.BodyHandlers.ofString();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return "Что-то пошло не так, код - " + response.statusCode();
            }
            return response.body();
        } catch (IOException | InterruptedException e) {
            return "Проверьте адрес";
        }
    }
}
