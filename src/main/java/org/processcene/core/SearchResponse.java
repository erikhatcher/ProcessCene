package org.processcene.core;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {
  // hits
  // errors
  public String error;
  public float max_score = 0.0f;

  public List<ResponseDocument> documents = new ArrayList<>();
  public String explain; // TODO: Implement?  For Atlas it's the explain(), for Solr it'd be the full debug node output, for Lucene (??)
  public String parsed_query;
  public long query_time = -1;

  public void print() {
    System.out.println("SearchResponse: ");
    System.out.println("  error: " + error);
    System.out.println("  parsed_query: " + parsed_query);
    System.out.println("  max_score: " + max_score);
    System.out.println("  explain: " + explain);

    for (ResponseDocument document : documents) {
      System.out.println("  * " + document.id + " " + document.score + " :: " + document.fields.get("_id") + " :: " + document.fields.get("title"));
    }

  }
}

//public class AtlasResponse {
//  public List<Document> documents = new ArrayList<>();
//  public Document explain = null;
//  public String error = null;
//}
