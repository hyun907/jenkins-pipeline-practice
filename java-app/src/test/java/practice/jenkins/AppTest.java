package practice.jenkins;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AppTest {
    private static HttpServer server;
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static String baseUrl;

    @BeforeAll
    static void startServer() throws Exception {
        server = App.createServer(new InetSocketAddress("127.0.0.1", 0));
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @AfterAll
    static void stopServer() {
        server.stop(0);
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path)).GET().build();
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void healthEndpoint() throws Exception {
        HttpResponse<String> response = get("/health");
        assertEquals(200, response.statusCode());
        assertEquals("{\"status\":\"ok\"}", response.body());
    }

    @Test
    void aboutEndpoint() throws Exception {
        HttpResponse<String> response = get("/about");
        assertEquals(200, response.statusCode());
        assertEquals("{\"name\":\"jenkins-java-practice\",\"version\":\"1.0.0\"}", response.body());
    }

    @Test
    void unknownPath() throws Exception {
        HttpResponse<String> response = get("/missing");
        assertEquals(404, response.statusCode());
        assertEquals("{\"error\":\"not found\"}", response.body());
    }
}
