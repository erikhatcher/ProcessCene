package org.processcene.core;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {
  // hits
  // errors
  public String error;
  public float max_score = 0.0f;

  public List<ResponseDocument> documents = new ArrayList<>();
}

//public class AtlasResponse {
//  public List<Document> documents = new ArrayList<>();
//  public Document explain = null;
//  public String error = null;
//}
