import java.util.*;
import java.io.*;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.net.InetSocketAddress;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class api {

    // Make list a static member variable
    static ArrayList<String> list = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // Print the contents of the ArrayList
        System.out.println("demo of profanityapi.com");

        JsonUtils.appendJsonToList("words.json", list);

        HttpServer server = HttpServer.create(new InetSocketAddress(80), 0);

        // Create a context for the root path
        server.createContext("/", new MEventHandler());

        // Set the executor to null to use the default executor
        server.setExecutor(null);

        // Add a shutdown hook to gracefully stop the server
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(0);
            System.out.println("Server stopped");
        }));

        // Start the server
        server.start();
        System.out.println("Server is listening on port 8080");
    }


    static class MEventHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Add CORS headers
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
           // exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
           // exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            // Handle OPTIONS request
            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1); // No content for OPTIONS requests
                return;
            }

            // Get the request URI
            String requestURI = exchange.getRequestURI().toString();

            // Extract the part after the first "/"
            String input = requestURI.substring(1);
	    try{
	      input = URLDecoder.decode(input, "UTF-8");
	    }
	    catch(UnsupportedEncodingException e){
	      e.printStackTrace();
	    }

	    input = input.replace("@", "a")
                         .replace("1", "i")
                         .replace("3", "e")
                         .replace("8", "b");


	    input = input.replaceAll("\\s+", "").toUpperCase();
            String response = "False";

            if (list.stream().anyMatch(input::contains)) {    
	       // System.out.println("yep thats a bad word");
                response = "True";
            }

            // Send a simple response back to the client
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    public static class JsonUtils {

        public static void appendJsonToList(String filePath, ArrayList<String> list) {
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
            } catch (IOException e) {
                System.err.println("Error reading JSON file: " + e.getMessage());
            }
        }
    }
}

