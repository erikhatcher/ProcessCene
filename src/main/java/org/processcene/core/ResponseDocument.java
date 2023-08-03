package org.processcene.core;

import java.util.HashMap;
import java.util.Map;

public class ResponseDocument {
  public int id; // the id (1 to ProcessCene.MAX_DOCS)
  public float score;
  public String explain;
  public Map<String,Object> fields = new HashMap<>();
}
