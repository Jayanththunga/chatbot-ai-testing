package eval;

public class ConsistencyEval {

   public boolean evaluate(int passCount, int totalRuns) {

      double passRate = (double) passCount / totalRuns;

      double threshold = Double.parseDouble(
            utility.ConfigReader.get("passThreshold")
      );

      return passRate >= threshold;
   }
}