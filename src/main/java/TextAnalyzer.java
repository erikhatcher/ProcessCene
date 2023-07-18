import java.util.List;
import java.util.Map;

abstract public class TextAnalyzer {
  abstract List<Map<String, Object>> analyzeString(String analyzer_name, String text);
  abstract List<String> getAnalyzerNames();
}
