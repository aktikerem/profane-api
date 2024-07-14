import java.util.*;
import java.io.*;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;

public class api {

    // Make list a static member variable
    static ArrayList<String> list = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // Print the contents of the ArrayList
        System.out.println("demo of profanityapi.com");

        try {
            JsonUtils.appendJsonToList("words.json", list);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);

        // Create a context for the root path
        server.createContext("/", new MEventHandler());

        // Set the executor to null to use the default executor
        server.setExecutor(null);

        // Start the server
        server.start();
        System.out.println("Server is listening on port 80");
    }

    static class MEventHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Get the request URI
            String requestURI = exchange.getRequestURI().toString();

            // Extract the part after the first "/"
            String input = requestURI.substring(1);
            input = input.replaceAll("\\s+", "").toUpperCase();
            String response = "False";

            if (list.stream().anyMatch(input::contains)) {
                System.out.println("yep thats a bad word");
                response = "True";
            }

            // Send a simple response back to the client
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static class JsonUtils {

        public static void appendJsonToList(String filePath, ArrayList<String> list) throws IOException {
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                StringBuilder jsonContent = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonContent.append(line);
                }

                // Removing the square brackets at the beginning and end
                String content = jsonContent.toString().trim();
                content = content.substring(1, content.length() - 1).trim();

                // Splitting the strings and adding to the ArrayList
                String[] strings = content.split(",");
                for (String str : strings) {
                    // Remove the quotes around each string
                    list.add(str.trim().replaceAll("^\"|\"$", "").toUpperCase());
                }
            }
        }
    }
}

