import java.util.HashMap;
import java.util.List;

abstract public class TextAnalyzer {
  abstract List<HashMap<String, Object>> analyzeString(String analyzer_name, String text);
  abstract List<String> getAnalyzerNames();
}
