package org.processcene.atlas;

import com.mongodb.client.model.search.SearchOperator;
import org.processcene.core.SearchRequest;

public class AtlasSearchRequest implements SearchRequest {

  public SearchOperator search_operator;

  public AtlasSearchRequest(SearchOperator so) {
    search_operator = so;
  }

  @Override
  public String toString() {
    return search_operator.toString();
  }
}
