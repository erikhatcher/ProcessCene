package org.processcene.solr;

import org.processcene.core.SearchRequest;

import java.util.HashMap;
import java.util.Map;

public class SolrSearchRequest implements SearchRequest {
  public final Map<String, String> params = new HashMap<>();

  public SolrSearchRequest(Map<String, String> params) {
    this.params.putAll(params);
  }

  @Override
  public String toString() {
    return params.toString();
  }

  @Override
  public String getLabel() {
    return null;
  }
}
