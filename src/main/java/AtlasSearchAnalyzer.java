import java.util.HashMap;
import java.util.List;

public class AtlasSearchAnalyzer extends TextAnalyzer {
  // Assign Atlas Search analyzer name to comparable Solr field type mappings
//    analyzers.set("lucene.keyword", "string");
//    analyzers.set("lucene.whitespace", "text_ws");
//    analyzers.set("lucene.standard", "text_general");
//    analyzers.set("lucene.english", "text_en");
//        analyzers.set("solr.phonetic_en", "phonetic_en");
//  analyzers.set("solr.text_cjk","text_cjk");
//        analyzers.set("solr.text_general_rev","text_general_rev");
//  println(analyzers);

  @Override
  List<HashMap<String, Object>> analyzeString(String analyzer_name, String text) {
    return null;
  }

  @Override
  List<String> getAnalyzerNames() {
    return null;
  }
}
