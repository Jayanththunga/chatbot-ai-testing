package chatbot;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import utility.ConfigReader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ChatbotClient {

   public static Response query(String prompt) {

      Map<String, Object> payload = new HashMap<>() {{
         put("model", ConfigReader.get("model"));
         put("temperature", Double.parseDouble(ConfigReader.get("temperature")));
         put("messages", Collections.singletonList(
               new HashMap<String, Object>() {{
                  put("role", "user");
                  put("content", prompt);
               }}
         ));
      }};

      return RestAssured
            .given().baseUri(ConfigReader.get("base-url"))
            .header("Authorization", "Bearer " + ConfigReader.get("openaiAPIKey"))
            .header("Content-Type", "application/json")
            .body(payload)
            .post();
   }
}