import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;

import java.util.ArrayList;
import java.util.List;

public class QueryParsingSlide extends BaseSlide {
  private List<String> queries = new ArrayList<String>();
  private int queries_index = 0;

  public QueryParsingSlide(String title, ProcessCene presentation) {
    super(title, presentation);

    queries.add("my query");
    queries.add("genre:drama AND cast:keanu");
    queries.add("\"a phrase\" AND (size:[5 TO 10] OR color:blue)");
    queries.add("genre:drama AND cast:keanu");
    queries.add("(bad syntax");
    queries.add("actor:keanu reeves");
  }

  @Override
  public void draw(int step) {
    String query_string = "";
    if (step > 0) {
      query_string = queries.get(step - 1);
    }

    float x = 0;
    float y = presentation.textAscent() + presentation.textDescent();
    presentation.text("Query: " + query_string, x, y);
    y += presentation.textAscent() + presentation.textDescent();

    if (!query_string.isEmpty()) {
      String parsed_query;
      try {
        QueryParser parser = new QueryParser("title", new StandardAnalyzer());
        Query query = parser.parse(query_string);
        parsed_query = query.toString();
      } catch (ParseException e) {
        parsed_query = e.getLocalizedMessage();
      }

      presentation.text("Parsed query: " + parsed_query, x, y);
    }

    super.draw(step);
  }

  @Override
  public int getNumberOfSteps() {
    return queries.size();
  }
}
