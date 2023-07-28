package org.processcene;

import java.util.List;
import java.util.Map;

abstract public class TextAnalyzer {
  protected abstract List<Map<String, Object>> analyzeString(String analyzer_name, String text);

  protected abstract List<String> getAnalyzerNames();
}
