package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import impl.ChatRoomImpl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import util.JsonUtil;

public class ChatServer {
    private static final int PORT = 8082;
    private static final ChatRoomImpl room = ChatRoomImpl.getInstance();

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/chat/subscribe", ChatServer::handleSubscribe);
            server.createContext("/chat/unsubscribe", ChatServer::handleUnsubscribe);
            server.createContext("/chat/message", ChatServer::handleMessage);
            server.createContext("/chat/messages", ChatServer::handleMessages);
            server.setExecutor(null);
            server.start();

            System.out.println("Serveur REST demarre sur http://localhost:" + PORT);
            System.out.println("Endpoints disponibles :");
            System.out.println("POST /chat/subscribe   {\"pseudo\":\"Alice\"}");
            System.out.println("POST /chat/message     {\"pseudo\":\"Alice\",\"message\":\"Bonjour\"}");
            System.out.println("GET  /chat/messages?lastIndex=0");
            System.out.println("POST /chat/unsubscribe {\"pseudo\":\"Alice\"}");
        } catch (IOException e) {
            System.err.println("Erreur serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleSubscribe(HttpExchange exchange) throws IOException {
        if (!requireMethod(exchange, "POST")) {
            return;
        }

        Map<String, String> body = readJsonBody(exchange);
        String result = room.subscribe(body.get("pseudo"));
        sendStatus(exchange, result);
    }

    private static void handleUnsubscribe(HttpExchange exchange) throws IOException {
        if (!requireMethod(exchange, "POST")) {
            return;
        }

        Map<String, String> body = readJsonBody(exchange);
        String result = room.unsubscribe(body.get("pseudo"));
        sendStatus(exchange, result);
    }

    private static void handleMessage(HttpExchange exchange) throws IOException {
        if (!requireMethod(exchange, "POST")) {
            return;
        }

        Map<String, String> body = readJsonBody(exchange);
        String result = room.postMessage(body.get("pseudo"), body.get("message"));
        sendStatus(exchange, result);
    }

    private static void handleMessages(HttpExchange exchange) throws IOException {
        if (!requireMethod(exchange, "GET")) {
            return;
        }

        Map<String, String> query = JsonUtil.parseQuery(exchange.getRequestURI().getQuery());
        int lastIndex = parseInt(query.get("lastIndex"), 0);
        String[] messages = room.getMessages(lastIndex);
        int nextIndex = lastIndex + messages.length;

        StringBuilder json = new StringBuilder();
        json.append("{\"status\":\"OK\",\"nextIndex\":").append(nextIndex).append(",\"messages\":[");
        for (int i = 0; i < messages.length; i++) {
            if (i > 0) {
                json.append(',');
            }
            json.append(JsonUtil.quote(messages[i]));
        }
        json.append("]}");

        sendJson(exchange, 200, json.toString());
    }

    private static boolean requireMethod(HttpExchange exchange, String expectedMethod) throws IOException {
        if (expectedMethod.equals(exchange.getRequestMethod())) {
            return true;
        }
        sendJson(exchange, 405, "{\"status\":\"ERROR\",\"message\":\"Methode non autorisee\"}");
        return false;
    }

    private static Map<String, String> readJsonBody(HttpExchange exchange) throws IOException {
        InputStream input = exchange.getRequestBody();
        String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        return JsonUtil.parseObject(body);
    }

    private static void sendStatus(HttpExchange exchange, String result) throws IOException {
        if (result.startsWith("ERROR")) {
            sendJson(exchange, 400, "{\"status\":\"ERROR\",\"message\":" + JsonUtil.quote(result) + "}");
        } else {
            sendJson(exchange, 200, "{\"status\":\"OK\"}");
        }
    }

    private static void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] response = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
