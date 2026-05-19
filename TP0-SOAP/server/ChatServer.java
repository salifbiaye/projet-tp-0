package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import impl.ChatRoomImpl;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import util.SoapUtil;

public class ChatServer {
    private static final int PORT = 8081;
    private static final ChatRoomImpl room = ChatRoomImpl.getInstance();

    public static void main(String[] args) {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/chat", ChatServer::handleChat);
            server.setExecutor(null);
            server.start();

            System.out.println("Serveur SOAP demarre sur http://localhost:" + PORT + "/chat");
            System.out.println("WSDL disponible sur http://localhost:" + PORT + "/chat?wsdl");
        } catch (IOException e) {
            System.err.println("Erreur serveur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleChat(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod()) && "wsdl".equals(exchange.getRequestURI().getQuery())) {
            send(exchange, 200, "text/xml; charset=UTF-8", wsdl());
            return;
        }

        if (!"POST".equals(exchange.getRequestMethod())) {
            send(exchange, 405, "text/xml; charset=UTF-8", fault("Methode non autorisee"));
            return;
        }

        try {
            String request = readBody(exchange);
            Document document = SoapUtil.parse(request);
            Element operation = SoapUtil.getSoapOperation(document);
            if (operation == null) {
                send(exchange, 400, "text/xml; charset=UTF-8", fault("Operation SOAP introuvable"));
                return;
            }

            String operationName = SoapUtil.localName(operation);
            String response;
            if ("subscribe".equals(operationName)) {
                response = SoapUtil.statusResponse(operationName, room.subscribe(SoapUtil.childText(operation, "pseudo")));
            } else if ("unsubscribe".equals(operationName)) {
                response = SoapUtil.statusResponse(operationName, room.unsubscribe(SoapUtil.childText(operation, "pseudo")));
            } else if ("postMessage".equals(operationName)) {
                response = SoapUtil.statusResponse(operationName, room.postMessage(
                        SoapUtil.childText(operation, "pseudo"),
                        SoapUtil.childText(operation, "message")));
            } else if ("getMessages".equals(operationName)) {
                int lastIndex = parseInt(SoapUtil.childText(operation, "lastIndex"), 0);
                String[] messages = room.getMessages(lastIndex);
                response = SoapUtil.messagesResponse(messages, lastIndex + messages.length);
            } else {
                response = fault("Operation inconnue: " + operationName);
            }

            send(exchange, 200, "text/xml; charset=UTF-8", response);
        } catch (Exception e) {
            send(exchange, 500, "text/xml; charset=UTF-8", fault(e.getMessage()));
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        InputStream input = exchange.getRequestBody();
        return new String(input.readAllBytes(), StandardCharsets.UTF_8);
    }

    private static void send(HttpExchange exchange, int statusCode, String contentType, String body) throws IOException {
        byte[] response = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(statusCode, response.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }

    private static String fault(String message) {
        return SoapUtil.envelope("<soap:Fault><faultcode>soap:Server</faultcode><faultstring>"
                + SoapUtil.escape(message) + "</faultstring></soap:Fault>");
    }

    private static int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    private static String wsdl() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<definitions xmlns=\"http://schemas.xmlsoap.org/wsdl/\" "
                + "xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" "
                + "xmlns:tns=\"" + SoapUtil.CHAT_NS + "\" "
                + "targetNamespace=\"" + SoapUtil.CHAT_NS + "\">"
                + "<service name=\"ChatRoomService\"><port name=\"ChatRoomPort\" binding=\"tns:ChatRoomBinding\">"
                + "<soap:address location=\"http://localhost:" + PORT + "/chat\"/>"
                + "</port></service>"
                + "<binding name=\"ChatRoomBinding\" type=\"tns:ChatRoomPortType\">"
                + "<soap:binding style=\"document\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>"
                + "<operation name=\"subscribe\"><soap:operation soapAction=\"subscribe\"/></operation>"
                + "<operation name=\"unsubscribe\"><soap:operation soapAction=\"unsubscribe\"/></operation>"
                + "<operation name=\"postMessage\"><soap:operation soapAction=\"postMessage\"/></operation>"
                + "<operation name=\"getMessages\"><soap:operation soapAction=\"getMessages\"/></operation>"
                + "</binding>"
                + "<portType name=\"ChatRoomPortType\">"
                + "<operation name=\"subscribe\"/><operation name=\"unsubscribe\"/>"
                + "<operation name=\"postMessage\"/><operation name=\"getMessages\"/>"
                + "</portType></definitions>";
    }
}
