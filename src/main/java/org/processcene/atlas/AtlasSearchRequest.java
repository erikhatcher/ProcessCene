package org.processcene.atlas;

import com.mongodb.client.model.search.SearchOperator;
import org.bson.json.JsonWriterSettings;
import org.processcene.core.SearchRequest;

public class AtlasSearchRequest implements SearchRequest {

  private final String label;
  public SearchOperator search_operator;
  public String query_string;

  public AtlasSearchRequest(String label, String query_string, SearchOperator so) {
    search_operator = so;
    this.label = label;
    this.query_string = query_string;
  }

  @Override
  public String toString() {
    return search_operator.toBsonDocument().toJson(JsonWriterSettings.builder().indent(true).build());
  }

  @Override
  public String getLabel() {
    return label;
  }
}
