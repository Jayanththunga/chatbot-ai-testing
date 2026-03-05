package chatbotTest;

import chatbot.ChatbotClient;
import com.aventstack.extentreports.Status;
import eval.ConsistencyEval;
import eval.KeywordEval;
import eval.SemanticEval;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import utility.ConfigReader;
import utility.ExtentManager;
import utility.GoogleSheetUtil;
import utility.TestListeners;

import java.util.List;

@Listeners(TestListeners.class)
public class ChatbotEvaluationTest {

   protected String spreadsheetId;

   @BeforeClass
   public void setup(){
      spreadsheetId = ConfigReader.get("spreadsheetId");
   }

   @DataProvider(name = "promptData")
   public Object[][] getPromptData() {

      List<List<Object>> sheetData = GoogleSheetUtil.readSheet(spreadsheetId, "PromptData");

      if (sheetData == null || sheetData.size() <= 1) {
         return new Object[0][0];
      }

      Object[][] data = new Object[sheetData.size() - 1][1];

      for (int i = 1; i < sheetData.size(); i++) {
         data[i - 1][0] = sheetData.get(i);
      }

      return data;
   }

   @Test(dataProvider = "promptData")
   public void testChatbot(List<Object> rowData){
      SoftAssert softAssert = new SoftAssert();

      String prompt = rowData.get(0).toString();

      ExtentManager.getTest().getModel().setName(prompt);
      ExtentManager.getTest().assignCategory(rowData.get(1).toString());

      KeywordEval keywordEval = new KeywordEval();
      SemanticEval semanticEval = new SemanticEval();
      ConsistencyEval consistencyEval = new ConsistencyEval();

      int iterations = Integer.parseInt(ConfigReader.get("iterations"));

      int passCount = 0;

      for(int i = 1; i <= iterations; i++){
         ExtentManager.getTest().log(Status.INFO, "Run #" + i);
         Response response = ChatbotClient.query(prompt);

         String responseText = response.jsonPath().getString("choices[0].message.content");

         ExtentManager.getTest().log(Status.INFO, responseText);

         boolean keywordResult = keywordEval.evaluate(responseText, rowData);
         if(keywordResult) ExtentManager.getTest().log(Status.PASS, "KeywordEval PASSED");
         else ExtentManager.getTest().log(Status.FAIL, "KeywordEval FAILED. Expected keywords: " + rowData.get(3));

         boolean semanticResult = semanticEval.evaluate(responseText, rowData);
         if(semanticResult) ExtentManager.getTest().log(Status.PASS, "SemanticEval PASSED");
          else ExtentManager.getTest().log(Status.FAIL, "SemanticEval FAILED. Expected intent: " + rowData.get(2));

         if(keywordResult && semanticResult){
            passCount++;
         }
      }


      boolean consistencyResult = consistencyEval.evaluate(passCount, iterations);
      ExtentManager.getTest().info("Consistency: " + passCount + "/" + iterations);

      if(consistencyResult) ExtentManager.getTest().pass("ConsistencyEval PASSED");
      else ExtentManager.getTest().fail("ConsistencyEval FAILED");


      softAssert.assertTrue(consistencyResult, "ConsistencyEval FAILED: " + passCount + "/" + iterations);
      softAssert.assertAll();
   }

}
