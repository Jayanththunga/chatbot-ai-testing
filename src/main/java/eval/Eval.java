package eval;

import java.util.List;

public interface Eval {

   boolean evaluate(String response, List<Object> rowData);

}