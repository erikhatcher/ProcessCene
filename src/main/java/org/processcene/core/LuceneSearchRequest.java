package org.processcene.core;

import org.apache.lucene.search.Query;

public class LuceneSearchRequest implements SearchRequest {
  public final Query query;

  public LuceneSearchRequest(Query q) {
    query = q;
  }

  @Override
  public String toString() {
    return query.toString();
  }

  @Override
  public String getLabel() {
    return null;
  }
}
