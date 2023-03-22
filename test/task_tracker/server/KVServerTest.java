package task_tracker.server;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import task_tracker.exeption.ManagerExchangeException;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class KVServerTest {
    private static KVServer server;
    private static HttpClient client;
    private static URL url;

    @BeforeAll
    static void beforeAll() throws IOException {
        int PORT = 8086;
        server = new KVServer("localhost", PORT);
        client = HttpClient.newHttpClient();
        server.start();
        url = new URL("http://localhost:" + PORT);
    }
    @AfterAll
    static void afterAll() {
        server.stop(0);
    }

    @Test
    void testCorrectPutAndPop() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/save/" + "key" + "?API_TOKEN=" + server.apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString("Test"))
                    .build();

            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ManagerExchangeException("Не удалось сохранить данные");
            }
        } catch (NullPointerException | IOException | InterruptedException | IllegalArgumentException e) {
            throw new ManagerExchangeException(e.getMessage());
        }

        assertEquals(server.data.get("key"), "Test",
                "KVserver сохраняет не корректно");

        String responseBody;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/load/" + "key" + "?API_TOKEN=" + server.apiToken))
                    .GET()
                    .build();
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                responseBody = response.body();
            } else if (response.statusCode() == 400) {
                responseBody = null;
            } else {
                throw new ManagerExchangeException("Не удалось получить данные");
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            throw new ManagerExchangeException(e.getMessage());
        }

        assertEquals(responseBody, "Test",
                "KVserver возвращает не корректно");
    }
}