package eval;

import java.util.Arrays;
import java.util.List;

public class KeywordEval implements Eval {

   @Override
   public boolean evaluate(String response, List<Object> rowData) {


      if (rowData.size() < 4) {
         return false;
      }

      String[] keywords = rowData.get(3).toString().split(",");
      for (String keyword : keywords) {

         if (response.toLowerCase().contains(keyword.trim().toLowerCase())) {
            return true;
         }

      }
      return false;
   }

}