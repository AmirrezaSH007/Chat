import java.io.IOException;
import java.net.URI;

class Handler implements URLHandler {
    // The one bit of state on the server: a chat log that will be manipulated by
    // various requests.
    StringBuilder chatLog = new StringBuilder();

    public String handleRequest(URI url) {
        if (url.getPath().equals("/")) {
            return chatLog.length() == 0 ? "Chat is empty." : chatLog.toString();
        } else if (url.getPath().contains("/add-message")) {
            String query = url.getQuery();
            if (query != null) {
                String[] params = query.split("&");
                Map<String, String> paramMap = new HashMap<>();
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        paramMap.put(keyValue[0], keyValue[1]);
                    }
                }
                String user = paramMap.get("user");
                String message = paramMap.get("s");
                if (user != null && message != null) {
                    message = message.replace("+", " ");  // Replace plus with space for correct spacing in chat
                    chatLog.append(user).append(": ").append(message).append("\n");
                    return chatLog.toString();
                }
            }
            return "Invalid request";
        } else {
            return "404 Not Found!";
        }
    }
}
class ChatServer {
    public static void main(String[] args) throws IOException {
        if(args.length == 0){
            System.out.println("Missing port number! Try any number between 1024 to 49151");
            return;
        }

        int port = Integer.parseInt(args[0]);

        Server.start(port, new Handler());
    }
}
