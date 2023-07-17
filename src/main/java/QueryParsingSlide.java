import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryparser.surround.query.SrndQuery;
import org.apache.lucene.search.Query;
import processing.event.KeyEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryParsingSlide extends ConsoleOutputSlideBase {
  private final List<String> parsers = new ArrayList<>();
  private final Map<String,List<String>> queries_by_parser = new HashMap<>();
  private int current_parser_index = 0;

  public QueryParsingSlide(String title, ProcessCene presentation) {
    super(title, presentation);

    List<String> classic_queries = new ArrayList<>();
    classic_queries.add("my query");
    classic_queries.add("genre:drama AND cast:keanu");
    classic_queries.add("\"a phrase\" AND (size:[5 TO 10] OR color:blue)");
    classic_queries.add("genre:drama AND cast:keanu");
    classic_queries.add("(bad syntax");
    classic_queries.add("actor:keanu reeves");
    parsers.add("Classic");
    queries_by_parser.put("Classic", classic_queries);

    List<String> surround_queries = new ArrayList<>();
    surround_queries.add("3W(a, b)");
    parsers.add("Surround");
    queries_by_parser.put("Surround", surround_queries);
  }

  private String getQueryString(String parser_name, int query_index) {
    String query_string = "";
    List<String> queries = queries_by_parser.get(parser_name);
    if (query_index > 0) {
      query_string = queries.get(query_index - 1);
    }
    return query_string;
  }

  @Override
  protected String getDescription(int step) {
    if (step == 0) {
      return "";
    }
    String parser_name = parsers.get(current_parser_index);
    String query_string = getQueryString(parser_name, step);

    String description = "Query Parser: " + parser_name + " :: Query: " + query_string;

    return description;
  }

  @Override
  protected String getOutput(int step) {
    if (step == 0) {
      return "";
    }
    String parser_name = parsers.get(current_parser_index);
    String query_string = getQueryString(parser_name, step);

    StringBuilder parsed_query = new StringBuilder();
    if (!query_string.isEmpty()) {
      try {
        Query query = null;
        // TODO: `if` statements here need to go.... abstract this to our own "query parser" abstraction like TextAnalyzer
        if (parser_name.equals("Classic")) {
          QueryParser parser = new QueryParser("title", new StandardAnalyzer());
          query = parser.parse(query_string);
        }
        if (parser_name.equals("Surround")) {
          SrndQuery srndQuery = org.apache.lucene.queryparser.surround.parser.QueryParser.parse(query_string);
          query = srndQuery.makeLuceneQueryField("title", new BasicQueryFactory());

        }
        parsed_query.append(query.toString());
        if (parsed_query.length() > 50) {
          parsed_query.insert(50, '\n');
        }
      } catch (Exception e) {
        parsed_query.append(e.getLocalizedMessage());
      }
    }
    return parsed_query.toString();
  }

  @Override
  public void keyTyped(KeyEvent event) {
    if (event.isAltDown() && event.isControlDown()) {
      switch (event.getKey()) {
        case ',':
          if (current_parser_index > 0) {
            current_parser_index--;
            reset();
          }
          break;

        case '.':
          if (current_parser_index <= parsers.size()) {
            current_parser_index++;
            reset();
          }
          break;
      }
    }
  }

  private void reset() {
    presentation.step = 0;
  }

  @Override
  public int getNumberOfSteps() {
    return queries_by_parser.get(parsers.get(current_parser_index)).size();
  }
}


