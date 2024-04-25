import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
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
    
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = handler.handleRequest(exchange.getRequestURI());
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}

class Handler implements URLHandler {
    private StringBuilder chatLog = new StringBuilder();

    @Override
    public String handleRequest(URI url) {
        if (url.getPath().equals("/")) {
            return chatLog.length() == 0 ? "Chat is empty." : chatLog.toString();
        } else if (url.getPath().contains("/add-message")) {
            String query = url.getQuery();
            if (query != null) {
                Map<String, String> paramMap = queryToMap(query);
                String user = paramMap.get("user");
                String message = paramMap.get("s");
                if (user != null && message != null) {
                    message = message.replace("+", " ");  // Replace plus with space
                    chatLog.append(user).append(": ").append(message).append("\n");
                    return chatLog.toString();
                }
            }
            return "Invalid request";
        } else {
            return "404 Not Found!";
        }
    }

    private Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2) {
                result.put(keyValue[0], keyValue[1].replace("+", " "));
            }
        }
        return result;
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
        server.createContext("/", new ServerHttpHandler(new Handler()));
        server.start();
        System.out.println("Server started on port " + port + ". Visit http://localhost:" + port + "/add-message?s=YourMessage&user=YourName to add messages.");
    }
}
