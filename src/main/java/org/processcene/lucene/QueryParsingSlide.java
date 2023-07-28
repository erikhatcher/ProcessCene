package org.processcene.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.queryparser.simple.SimpleQueryParser;
import org.apache.lucene.queryparser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryparser.surround.query.SrndQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import org.processcene.BaseSlide;
import org.processcene.ProcessCene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryParsingSlide extends BaseSlide {
  private final List<String> parsers = new ArrayList<>();
  private final Map<String, List<String>> queries_by_parser = new HashMap<>();

  public QueryParsingSlide(String title, ProcessCene presentation) {
    super(title, presentation);

    List<String> classic_queries = new ArrayList<>();
    classic_queries.add("foo");
    classic_queries.add("foo*");
    classic_queries.add("foo~");
    classic_queries.add("size:[5 TO 10]");
    classic_queries.add("\"phrase query\"");
    classic_queries.add("\"phrase query\"~3");
    classic_queries.add("\"phrase query\"~-1");
    classic_queries.add("\"(jo* -john)  smith\"");
    classic_queries.add("loose terms");
    classic_queries.add("genre:drama AND cast:keanu");
    classic_queries.add("(bad syntax");
    classic_queries.add("actor:keanu reeves");
    parsers.add("Classic");
    queries_by_parser.put("Classic", classic_queries);

    List<String> simple_queries = new ArrayList<>();
    simple_queries.add("\"phrase query\"~-1");
    simple_queries.add("foo bar baz");
    simple_queries.add("(open paren no close");
    simple_queries.add("some ran*&dom !~-* stuff");
    parsers.add("Simple");
    queries_by_parser.put("Simple", simple_queries);

    List<String> complex_phrase_queries = new ArrayList<>();
    complex_phrase_queries.add("\"(jo* -john)  smith\"");
    parsers.add("ComplexPhrase");
    queries_by_parser.put("ComplexPhrase", complex_phrase_queries);

    List<String> surround_queries = new ArrayList<>();
    surround_queries.add("3W(a, b)");
    parsers.add("Surround");
    queries_by_parser.put("Surround", surround_queries);
  }

  @Override
  public void draw(int step) {
    String description = getDescription(step);
    String output = getOutput(step);

    float x = 50;
    float y = presentation.textAscent() + presentation.textDescent();
    presentation.text(description, x, y);
    y += presentation.textAscent() + presentation.textDescent() + 100;

    presentation.text(output, x, y);
    y += presentation.textAscent() + presentation.textDescent();

    super.draw(step);
  }

  private String getQueryString(String parser_name, int query_index) {
    String query_string = "";
    List<String> queries = queries_by_parser.get(parser_name);
    if (query_index > 0) {
      query_string = queries.get(query_index - 1);
    }
    return query_string;
  }

  protected String getDescription(int step) {
    String parser_name = parsers.get(getCurrentVariationIndex());
    String query_string = getQueryString(parser_name, step);

    String qs = "";
    if (step > 0) {
      qs = "\nQuery: " + query_string;
    }

    String description = "Query Parser: " + parser_name + qs;

    return description;
  }

  protected String getOutput(int step) {
    if (step == 0) {
      return "";
    }
    String parser_name = parsers.get(getCurrentVariationIndex());
    String query_string = getQueryString(parser_name, step);

    StringBuilder parsed_query = new StringBuilder();
    if (!query_string.isEmpty()) {
      try {
        Query query = null;
        // TODO: `if` statements here need to go.... abstract this to our own "query parser" abstraction like org.processcene.TextAnalyzer
        if (parser_name.equals("Classic")) {
          QueryParser parser = new QueryParser("title", new StandardAnalyzer());
          query = parser.parse(query_string);
        }

        if (parser_name.equals("Surround")) {
          SrndQuery srndQuery = org.apache.lucene.queryparser.surround.parser.QueryParser.parse(query_string);
          query = srndQuery.makeLuceneQueryField("title", new BasicQueryFactory());
        }
        if (parser_name.equals("Simple")) {
          Map<String, Float> weights = new HashMap<>();
          weights.put("title", 5.0f);
          weights.put("category", 2.0f);
          SimpleQueryParser sqp = new SimpleQueryParser(new StandardAnalyzer(), weights);
          sqp.setDefaultOperator(BooleanClause.Occur.MUST);
          query = sqp.parse(query_string);

          System.out.println("\n\n" + query_string + ":");
          System.out.println(query.toString());
        }
        if (parser_name.equals("ComplexPhrase")) {
          ComplexPhraseQueryParser cpqp = new ComplexPhraseQueryParser("title", new StandardAnalyzer());
          query = cpqp.parse(query_string);
        }

        parsed_query.append("Query class: " + query.getClass().getName() + "\n\n");
        parsed_query.append(query.toString());
        if (parsed_query.length() > 200) {
          parsed_query.insert(200, '\n');
        }
      } catch (Exception e) {
        parsed_query.append("ERROR:" + e.getLocalizedMessage());
      }
    }
    return parsed_query.toString();
  }

  @Override
  public int getNumberOfSteps() {
    return queries_by_parser.get(parsers.get(getCurrentVariationIndex())).size();
  }

  @Override
  public int getNumberOfVariations() {
    return parsers.size();
  }
}


