package eval;

import chatbot.ChatbotClient;
import io.restassured.response.Response;

import java.util.List;

public class SemanticEval implements Eval {

   @Override
   public boolean evaluate(String response, List<Object> rowData) {

      if (rowData.size() < 3) return false;

      String expectedIntent = rowData.get(2).toString();

      String judgePrompt =
            "User Prompt: " + rowData.get(0).toString() + "\n\n" +
                  "Expected Intent: " + expectedIntent + "\n\n" +
                  "AI Response: " + response + "\n\n" +
                  "Evaluate if the response satisfies the expected intent.\n" +
                  "Return only PASS or FAIL.";

      Response judgeResponse = ChatbotClient.query(judgePrompt);

      String verdict =
            judgeResponse.jsonPath().getString("choices[0].message.content");

      return verdict != null && verdict.toUpperCase().contains("PASS");
   }
}