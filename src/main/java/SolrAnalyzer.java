import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SolrAnalyzer extends TextAnalyzer {

  private String[] field_types = { "string", "text_ws", "text_general", "text_en",
                                   "text_en_splitting", "text_en_splitting_tight",
                                   "text_cjk", "phonetic_en", "text_general_rev"};
  @Override
  List<HashMap<String, Object>> analyzeString(String analyzer_name, String text) {
    String field_type = analyzer_name;

    // NOTE: /analysis/field can take a comma-separated analysis.fieldtype value of multiple field types
    JSONObject analysis_response;
    try {
      URL solr_url = new URL("http://localhost:8983/solr/stc/analysis/field?analysis.fieldtype=" + field_type + "&analysis.fieldvalue=" + java.net.URLEncoder.encode(text) + "&verbose_output=1&wt=json&json.nl=arrarr");
      BufferedReader in = new BufferedReader(
          new InputStreamReader(solr_url.openStream()));
      analysis_response = new JSONObject(in);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    JSONArray index_analysis = analysis_response.getJSONObject("analysis").getJSONObject("field_types").getJSONObject(field_type).getJSONArray("index");
    JSONArray final_tokens = index_analysis.getJSONArray(index_analysis.size()-1).getJSONArray(1);

    List<HashMap<String,Object>> tokens = new ArrayList<HashMap<String,Object>>();
    int last_token_position = 0; // Solr computes and returns position, hiding position_increment
    for (int i=0; i < final_tokens.size(); i++) {
      JSONObject solr_token = final_tokens.getJSONObject(i);

      HashMap<String,Object> token = new HashMap<String,Object>();
      int position = solr_token.getInt("position");
      token.put("term", solr_token.getString("text"));
      token.put("bytes", solr_token.getString("raw_bytes"));
      token.put("start_offset", solr_token.getInt("start"));
      token.put("end_offset", solr_token.getInt("end"));
      token.put("position", position);
      token.put("position_length", solr_token.getInt("org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute#positionLength"));
      token.put("type", solr_token.getString("type"));
      token.put("term_frequency",solr_token.getInt("org.apache.lucene.analysis.tokenattributes.TermFrequencyAttribute#termFrequency"));

      // Hack: compute position_increment since Solr hides that from us, computing position instead
      token.put("position_increment", position - last_token_position);
      tokens.add(token);

      last_token_position = position;
    }

    return tokens;
  }

  @Override
  List<String> getAnalyzerNames() {
    return Arrays.stream(field_types).toList();
  }
}

/*
class SolrAnalyzer extends TextAnalyzer {
  SolrAnalyzer(String t, String a) {
    super(t,a);
  }

   List<HashMap<String,Object>> analyzeValue() {
     String field_type = analyzer;

     // NOTE: /analysis/field can take a comma-separated analysis.fieldtype value of multiple field types
     JSONObject analysis_response = loadJSONObject("http://localhost:8983/solr/stc/analysis/field?analysis.fieldtype=" + field_type + "&analysis.fieldvalue=" + java.net.URLEncoder.encode(text) + "&analysis.query=brown&analysis.showmatch=true&verbose_output=1&wt=json&json.nl=arrarr");

     JSONArray index_analysis = analysis_response.getJSONObject("analysis").getJSONObject("field_types").getJSONObject(field_type).getJSONArray("index");
     JSONArray final_tokens = index_analysis.getJSONArray(index_analysis.size()-1).getJSONArray(1);

     List<HashMap<String,Object>> tokens = new ArrayList<HashMap<String,Object>>();
     for (int i=0; i < final_tokens.size(); i++) {
       JSONObject solr_token = final_tokens.getJSONObject(i);

       HashMap<String,Object> token = new HashMap<String,Object>();
       token.put("term", solr_token.getString("text"));
       token.put("bytes", solr_token.getString("raw_bytes"));
       token.put("start_offset", solr_token.getInt("start"));
       token.put("end_offset", solr_token.getInt("end"));
       token.put("position", solr_token.getInt("position"));
       token.put("position_length", solr_token.getInt("org.apache.lucene.analysis.tokenattributes.PositionLengthAttribute#positionLength"));
       token.put("type", solr_token.getString("type"));
       token.put("term_frequency",solr_token.getInt("org.apache.lucene.analysis.tokenattributes.TermFrequencyAttribute#termFrequency"));
       tokens.add(token);
     }

     return tokens;
   }
}

// Leftover old code, calls to Solr with multiple field types at once:

//  String[] solr_field_types = analyzers.valueArray();
//  String field_type_list = String.join(",",solr_field_types);
//  println(field_type_list);

//  JSONObject analysis_response;
//  if (offline_mode) {
//    analysis_response = loadJSONObject("data/solr_analysis_default.json");
//  } else {
//    analysis_response = loadJSONObject("http://localhost:8983/solr/stc/analysis/field?analysis.fieldtype=" + java.net.URLEncoder.encode(field_type_list) + "&analysis.fieldvalue=" + java.net.URLEncoder.encode(text) + "&analysis.query=brown&analysis.showmatch=true&verbose_output=1&wt=json&json.nl=arrarr");
//    // saveJSONObject(analysis_response, "data/analysis_response_" + year() + "_" + month() + "_" + day() + "_" + hour() + "_" + minute() + "_" + second());
//  }

//  println(analysis_response);
//  for (int i=0; i < solr_field_types.length; i++) {
//    String field_type = solr_field_types[i];
//    println(analysis_response.getJSONObject("analysis").getJSONObject("field_types").getJSONObject(field_type).getJSONArray("index"),"<==");
//    JSONArray index_analysis = analysis_response.getJSONObject("analysis").getJSONObject("field_types").getJSONObject(field_type).getJSONArray("index");

//    println(index_analysis,"<==",field_type);
    //analysis.put(field_type, index_analysis);
//  }

//  println(solr_field_types);
//  println(analyzer_index);
//  println("field type:",field_type);

//  String field_type = solr_field_types[analyzer_index];
//  JSONArray index_analysis = analysis.get(field_type);
//  final_tokens = index_analysis.getJSONArray(index_analysis.size()-1).getJSONArray(1);

 */