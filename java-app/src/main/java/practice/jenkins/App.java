package practice.jenkins;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public final class App {
    private App() {}

    public static HttpServer createServer(InetSocketAddress address) throws IOException {
        HttpServer server = HttpServer.create(address, 0);
        server.createContext("/", App::handle);
        return server;
    }

    private static void handle(HttpExchange exchange) throws IOException {
        int status;
        String response;
        if (!"GET".equals(exchange.getRequestMethod())) {
            status = 405;
            response = "{\"error\":\"method not allowed\"}";
            exchange.getResponseHeaders().set("Allow", "GET");
        } else {
            switch (exchange.getRequestURI().getPath()) {
                case "/" -> {
                    status = 200;
                    response = "{\"message\":\"Hello from a Java Jenkins practice app\"}";
                }
                case "/health" -> {
                    status = 200;
                    response = "{\"status\":\"ok\"}";
                }
                case "/about" -> {
                    status = 200;
                    response = "{\"name\":\"jenkins-java-practice\",\"version\":\"1.0.0\"}";
                }
                default -> {
                    status = 404;
                    response = "{\"error\":\"not found\"}";
                }
            }
        }
        byte[] body = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, body.length);
        try (var output = exchange.getResponseBody()) {
            output.write(body);
        }
    }

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = createServer(new InetSocketAddress("0.0.0.0", port));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop(0)));
        server.start();
        System.out.println("Listening on port " + port);
        new CountDownLatch(1).await();
    }
}
