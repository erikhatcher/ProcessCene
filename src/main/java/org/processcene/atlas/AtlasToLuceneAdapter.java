package org.processcene.atlas;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.simple.SimpleQueryParser;
import org.apache.lucene.search.Query;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.processcene.core.LuceneAdapter;
import org.processcene.core.LuceneSearchRequest;
import org.processcene.core.ProcessCene;
import org.processcene.core.SearchRequest;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * `queryString` enhancements PoC
 * <p>
 * Use the AtlasAdapter to get a set of documents, but index them into Lucene
 * and craft Lucene queries from a JSON file specification
 */
public class AtlasToLuceneAdapter extends LuceneAdapter {
  public AtlasToLuceneAdapter(AtlasAdapter engine) {
    super(engine.getDocuments());
  }

  // Used to:  @Override
  public List<SearchRequest> getQueriesOld() {
    List<SearchRequest> queries = new ArrayList<>();

    // load JSON
    JSONParser parser = new JSONParser();
    try {
      JSONObject qs_data = (JSONObject) parser.parse(new FileReader(ProcessCene.getFilePathFromResources("atlas/queryStrings.json")));
      JSONArray qs_examples = (JSONArray) qs_data.get("examples");

      for (int i = 0; i < qs_examples.size(); i++) {
        JSONObject example = (JSONObject) qs_examples.get(i);
        JSONObject qs = (JSONObject) example.get("queryString");

        String query = (String) qs.get("query");
        Query q = null;
        Analyzer analyzer = new StandardAnalyzer();

        if (qs.get("defaultPath") == null) {
          // Handle PoC queryString syntax (no top-level `defaultPath`)

          if (qs.get("classic") != null) {
            JSONObject classic = (JSONObject) qs.get("classic");
            QueryParser qp = new QueryParser((String) classic.get("defaultPath"), analyzer);
            q = qp.parse(query);
          }

          if (qs.get("simple") != null) {
            JSONObject simple = (JSONObject) qs.get("simple");
            Map<String, Float> field_weights = new HashMap<>();

            // TODO: how to handle field weights missing?  allow a single field, no weights?
            JSONObject fieldWeights = (JSONObject) simple.get("fieldWeights");
            for (Object o : fieldWeights.keySet()) {
              String field_name = (String) o;
              float weight = ((Double) fieldWeights.get(field_name)).floatValue();
              field_weights.put(field_name, weight);
            }

            // default flags
            int flags = SimpleQueryParser.PHRASE_OPERATOR |
                SimpleQueryParser.AND_OPERATOR |
                SimpleQueryParser.OR_OPERATOR |
                SimpleQueryParser.NOT_OPERATOR;
            JSONObject options = (JSONObject) simple.get("options");
            if (options != null) {
              for (Object o : options.keySet()) {
                String option_name = (String) o;
                boolean turn_on = (Boolean) options.get(option_name);
                int option_bit = 0;
                switch (option_name) {
                  case "andOperator":
                    option_bit = SimpleQueryParser.AND_OPERATOR;
                    break;
                  case "orOperator":
                    option_bit = SimpleQueryParser.OR_OPERATOR;
                    break;
                  case "notOperator":
                    option_bit = SimpleQueryParser.NOT_OPERATOR;
                    break;
                  case "prefixOperator":
                    option_bit = SimpleQueryParser.PREFIX_OPERATOR;
                    break;
                  case "phraseOperator":
                    option_bit = SimpleQueryParser.PHRASE_OPERATOR;
                    break;
                  case "precedenceOperators":
                    option_bit = SimpleQueryParser.PRECEDENCE_OPERATORS;
                    break;
                  case "escapeOperator":
                    option_bit = SimpleQueryParser.ESCAPE_OPERATOR;
                    break;
                  case "whitespaceOperator":
                    option_bit = SimpleQueryParser.WHITESPACE_OPERATOR;
                    break;
                  case "fuzzyOperator":
                    option_bit = SimpleQueryParser.FUZZY_OPERATOR;
                    break;
                  case "nearOperator":
                    option_bit = SimpleQueryParser.NEAR_OPERATOR;
                    break;
                  default:
                    System.out.println("Unknown SimpleQueryParser option: " + option_name);
                }

                if (turn_on) {
                  flags |= option_bit;
                } else {
                  flags ^= option_bit;
                }
              }
            }

            // TODO: Add defaultOperator: "AND|OR"
            SimpleQueryParser sqp = new SimpleQueryParser(analyzer, field_weights, flags);
            //sqp.setDefaultOperator(BooleanClause.Occur.MUST);
            q = sqp.parse(query);
          }

        } else {
          // Handle current queryString syntax: { defaultPath: "...", query: "..." }
          QueryParser qp = new QueryParser((String) qs.get("defaultPath"), analyzer);
          q = qp.parse(query);
        }

        queries.add(new LuceneSearchRequest(q));
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return queries;
  }

}
