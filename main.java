import java.util.*;
import java.io.*;

public class api {


        public static void main(String[] args){


        ArrayList<String> list = new ArrayList<>();

        // Print the contents of the ArrayList
        System.out.println("demo of profanityapi.com");

        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.println("Enter ");

        String input = myObj.nextLine();  // Read user inputi
        input = input.replaceAll("\\s+","");

        try {
            JsonUtils.appendJsonToList("words.json", list);

        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i = 0; i<list.size();i++){
          if(input.toUpperCase().contains(list.get(i).toUpperCase())){
            System.out.println("yep thats a bad word");
            break;
          }
        }
                                          



    }
}

public class JsonUtils {

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
                list.add(str.trim().replaceAll("^\"|\"$", ""));
            }
        }
    }
}
