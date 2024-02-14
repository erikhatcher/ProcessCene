package org.processcene;

import com.mongodb.client.model.search.SearchOperator;
import com.mongodb.client.model.search.SearchPath;
import org.processcene.atlas.AtlasSearchRequest;
import org.processcene.core.BaseSlide;
import org.processcene.core.ProcessCene;
import org.processcene.core.SearchEngineAdapter;
import org.processcene.core.SearchResponse;

public class QueryRunnerSlide extends BaseSlide {
  private final SearchEngineAdapter engine;
  private final String[] queries;

  public QueryRunnerSlide(String title, SearchEngineAdapter engine, String[] queries) {
    super(title);
    this.engine = engine;
    this.queries = queries;
  }

  @Override
  public void draw(ProcessCene p, int step) {
    int query_index = p.frameCount % queries.length;

    String query_string = queries[query_index];
    SearchOperator operator = SearchOperator.text(SearchPath.wildcardPath("*"), query_string);
    AtlasSearchRequest asr = new AtlasSearchRequest(operator.toString(), query_string, operator);
    SearchResponse response = engine.search(asr);

    p.text(query_string, 50, 50);
    p.text(response.documents.size() + " found in " + response.query_time + "ms", 50, 100);

    super.draw(p, step);
  }
}
