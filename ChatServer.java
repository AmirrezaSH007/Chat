import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

class Handler implements URLHandler {
    // State on the server: the chat messages
    List<String> chatMessages = new ArrayList<>();

    public String handleRequest(URI url) {
        // Check if the path is for adding a message
        if (url.getPath().contains("/add-message")) {
            String query = url.getQuery();
            if (query != null) {
                String[] queryParams = query.split("&");
                String user = null;
                String message = null;
                for (String param : queryParams) {
                    String[] keyValue = param.split("=");
                    if ("user".equals(keyValue[0])) {
                        user = keyValue[1];
                    } else if ("s".equals(keyValue[0])) {
                        message = keyValue[1];
                    }
                }
                if (user != null && message != null) {
                    chatMessages.add(user + ": " + message);
                    return String.join("\n", chatMessages);
                }
            }
            return "Invalid request";
        } else {
            // If not adding a message, return the chat log or a welcome message
            return chatMessages.isEmpty() ? "Welcome to the chat server!" : String.join("\n", chatMessages);
        }
    }
}

// Rename NumberServer to ChatServer
public class ChatServer {
    public static void main(String[] args) throws IOException {
        if(args.length == 0) {
            System.out.println("Missing port number! Try any number between 1024 to 49151");
            return;
        }

        int port = Integer.parseInt(args[0]);
        Server.start(port, new Handler());
    }
}
