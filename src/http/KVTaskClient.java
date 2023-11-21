package http;

import com.yandex.kanban.exeption.RequestFileException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KVTaskClient {

    private String url;
    private String apiToken;
    private HttpClient client;

    public KVTaskClient(int port) {
        url = "http://localhost:" + port;
        apiToken = register(url);
        client = HttpClient.newHttpClient();
    }

    private String register(String url) {
        try {

            this.url = url;

            URI uri = URI.create(this.url + "/register");

            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString()
            );

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                throw new RequestFileException("Cant do register request, status code " + response.statusCode());
            }

        } catch (IOException | InterruptedException exception) {
            throw new RequestFileException("Cant do request");
        }

    }

    public void put(String key, String value) {

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString(value))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RequestFileException("Cant do register request, status code " + response.statusCode());
            }
        } catch (IOException | InterruptedException exception) {
            throw new RequestFileException("Cant do request");
        } catch (RequestFileException e) {
            throw new RuntimeException(e);
        }
    }

    public String load (String key) throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(this.url + "/load/" + key + "?API_TOKEN=" + this.apiToken))
                .header("Content-Type", "application/json").build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        return response.body();
    }

}
