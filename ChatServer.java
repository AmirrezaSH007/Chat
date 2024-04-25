import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

interface URLHandler {
    String handleRequest(URI url);
}

class ServerHttpHandler implements HttpHandler {
    URLHandler handler;
    
    ServerHttpHandler(URLHandler handler) {
        this.handler = handler;
    }
    
    public void handle(final HttpExchange exchange) throws IOException {
        try {
            String response = handler.handleRequest(exchange.getRequestURI());
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        } catch (Exception e) {
            String response = "Internal Server Error: " + e.getMessage();
            exchange.sendResponseHeaders(500, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        }
    }
}

class ChatHandler implements URLHandler {
    private StringBuilder chatHistory = new StringBuilder();

    public String handleRequest(URI url) {
        if (url.getPath().equals("/add-message")) {
            String query = url.getQuery();
            String[] params = query.split("&");
            String message = params[0].split("=")[1].replace("+", " ");
            String user = params[1].split("=")[1].replace("+", " ");
            chatHistory.append(user).append(": ").append(message).append("\n");
            return chatHistory.toString();
        } else {
            return "404 Not Found!";
        }
    }
}

public class ChatServer {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Missing port number! Try any number between 1024 to 49151");
            return;
        }
        int port = Integer.parseInt(args[0]);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new ServerHttpHandler(new ChatHandler()));
        server.start();
        System.out.println("Server started on port " + port + ". Visit http://localhost:" + port + "/add-message?s=YourMessage&user=YourName to add messages.");
    }
}
