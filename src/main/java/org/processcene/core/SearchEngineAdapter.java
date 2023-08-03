package org.processcene.core;

import org.processcene.DocumentAvatar;

import java.util.List;

public interface SearchEngineAdapter {
  SearchResponse search(SearchRequest request);

  List<SearchRequest> getQueries();

  List<DocumentAvatar> getDocuments();
}
