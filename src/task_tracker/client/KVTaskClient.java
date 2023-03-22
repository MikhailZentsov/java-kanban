package task_tracker.client;

import task_tracker.exeption.ManagerExchangeException;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final URL url;
    private final String API_KEY;
    private final HttpClient client;

    public KVTaskClient(URL url) {
        this.client = HttpClient.newHttpClient();
        this.url = url;
        this.API_KEY = getAPI_KEY();
    }

    public void put(String key, String json) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/save/" + key + "?API_TOKEN=" + API_KEY))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerExchangeException("Не удалось сохранить данные");
            }
        } catch (NullPointerException | IOException | InterruptedException | IllegalArgumentException e) {
            throw new ManagerExchangeException(e.getMessage());
        }
    }

    public String load(String key) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/load/" + key + "?API_TOKEN=" + API_KEY))
                .GET()
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else if (response.statusCode() == 400) {
                return null;
            } else {
                throw new ManagerExchangeException("Не удалось получить данные");
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            throw new ManagerExchangeException(e.getMessage());
        }
    }

    private String getAPI_KEY() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/register"))
                .GET()
                .build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new ManagerExchangeException("Не удалось получить API_KEY");
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            throw new ManagerExchangeException(e.getMessage());
        }
    }
}
