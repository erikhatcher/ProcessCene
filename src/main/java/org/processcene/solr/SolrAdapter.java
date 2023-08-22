package org.processcene.solr;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.processcene.core.DocumentAvatar;
import org.processcene.core.ResponseDocument;
import org.processcene.core.SearchEngineAdapter;
import org.processcene.core.SearchRequest;
import org.processcene.core.SearchResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolrAdapter implements SearchEngineAdapter {
  private final String solr_url;
  private final String collection;
  private List<DocumentAvatar> documents = new ArrayList<>();
  private final Map<String, Integer> doc_map = new HashMap<>();
  private final List<SearchRequest> queries = new ArrayList<>();

  public SolrAdapter(String solr_url, String collection) {
    // Query Solr for all docs, make into avatars
    this.solr_url = solr_url;
    this.collection = collection;

    Map<String, String> select_and_return_all = new HashMap<>();
    select_and_return_all.put("q", "*:*");        // Match all documents
    select_and_return_all.put("rows", "9999999"); // Return all documents
    select_and_return_all.put("fl", "*");         // Return ll fields of each document
    select_and_return_all.put("debug", "true");     // Useful stuff here!

    JSONArray json_docs = null;
    try {
      json_docs = solr_select(select_and_return_all);
    } catch (Exception e) {
      // TODO: what to do with an exception here?
      throw new RuntimeException(e);
    }

    for (int i = 0; i < json_docs.size(); i++) {
      Object jd = json_docs.get(i);
      JSONObject json_doc = (JSONObject) jd;
      Map<String, Object> doc_fields = new HashMap<>();
      int index = i + 1;
      doc_fields.put("id", index);
      doc_fields.put("title", ((JSONArray) json_doc.get("title_txt")).get(0));
      doc_fields.put("type", json_doc.get("type_s"));

// Add the document, and map it by Solr `id`
      documents.add(new DocumentAvatar(doc_fields));
      doc_map.put(String.valueOf(json_doc.get("id")), index); // cross-ref to know where an Solr id (Atlas _id, same diff) is in the full list
    }


    // D'oh: TODO -
    //    ==> Query Field 'cast' is not a valid field name
    Map<String, String> simple_params = new HashMap<>();
    simple_params.put("defType", "simple");
    simple_params.put("qf", "title_txt^5 fullplot_txt^2 cast_txt^1");
    simple_params.put("debug", "true");
    simple_params.put("fl", "*,score,explain:[explain style=text]");

    Map<String, String> edismax_params = new HashMap<>();
    edismax_params.put("defType", "edismax");
    edismax_params.put("qf", "title_txt^5 fullplot_txt^2 cast_txt^1");
    edismax_params.put("pf", "title_txt fullplot_txt cast_txt");
    edismax_params.put("debug", "true");
    edismax_params.put("fl", "*,score,explain:[explain style=text]");
    edismax_params.put("pf2", "title_txt fullplot_txt cast_txt");
    edismax_params.put("pf3", "title_txt fullplot_txt cast_txt");
//    edismax_params.put("mm","2<-25% 9<-3");
//    edismax_params.put("lowercaseOperators","true");
//    edismax_params.put("tie","0.1");
//    edismax_params.put("ps","5");
//    edismax_params.put("ps2","0");
//    edismax_params.put("ps3","0");
//    edismax_params.put("stopwords","false");
//    edismax_params.put("bq","+ boost query");
//    edismax_params.put("bf","+ boost function");
//    edismax_params.put("boost","* function");
//    edismax_params.put("uf","see docs, what's a good example?");
//    edismax_params.put("mm.autoRelax","true");


    // Create a list of Solr queries
    Map<String, String> params = new HashMap<>();
    params.putAll(edismax_params);
    params.put("q", "purple rain");
    queries.add(new SolrSearchRequest(params));

    params.clear();
    params.putAll(simple_params);
    params.put("q", "purple rain");
    queries.add(new SolrSearchRequest(params));

    params.clear();
    params.putAll(edismax_params);
    params.put("q", "the purple rain");
    queries.add(new SolrSearchRequest(params));

    params.clear();
    params.putAll(simple_params);
    params.put("q", "the purple rain");
    queries.add(new SolrSearchRequest(params));

    params.clear();
    params.putAll(simple_params);
    params.put("q", "the blues brothers");
    queries.add(new SolrSearchRequest(params));

    params.clear();
    params.putAll(simple_params);
    params.put("q", "tears in the rain");
    queries.add(new SolrSearchRequest(params));

    params.clear();
    params.putAll(edismax_params);
    params.put("q", "tears in the rain");
    queries.add(new SolrSearchRequest(params));
  }

  private JSONArray solr_select(Map<String, String> params) throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = null;
    try {
      request = HttpRequest.newBuilder()
          .uri(URI.create(this.solr_url + "/" + this.collection + "/select?" +
              map_to_query_string(params)))
          .GET()
          .build();
    } catch (UnsupportedEncodingException e) {
      // d'oh!
      System.out.println("SolrAdapter.solr_select EXCEPTION: " + e.getMessage());
      throw new RuntimeException(e);
    }

    JSONArray json_docs = new JSONArray();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    String response_body = response.body();
    if (response.statusCode() != 200) {
      throw new RuntimeException("Solr 404: " + response.body());
    }

//    System.out.println("# documents = " + documents.size());

    JSONObject json_response;
    try {
      json_response = (JSONObject) new JSONParser().parse(response_body);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
    json_docs = (JSONArray) ((JSONObject) json_response.get("response")).get("docs");

    // Solr pain point: get the explain and add to every doc, but can also get the debug / query parsed details / facets, etc.
    //json_response.get()
//    for (Object o : json_docs) {
//      JSONObject json_doc = (JSONObject) o;
//      json_response.get("debug").get("explain").get(json_doc.get("id"));
//      json_doc
//    }


    return json_docs;
  }

  @Override
  public SearchResponse search(SearchRequest req) {
    SolrSearchRequest request = (SolrSearchRequest) req;
    SearchResponse response = new SearchResponse();

    JSONArray search_results = new JSONArray();
    try {
      search_results = solr_select(request.params);
    } catch (Exception e) {
      response.error = e.getMessage();
    }

    for (int i = 0; i < search_results.size(); i++) {
      JSONObject hit = (JSONObject) search_results.get(i);
      String solr_id = hit.get("id").toString();

      ResponseDocument rd = new ResponseDocument();

      int index = doc_map.get(solr_id);
//      DocumentAvatar documentAvatar = documents.get(index); // borrowed from AtlasAdapter, but don't need here as Solr is returning all fields
      rd.id = index;

      rd.fields.put("id", solr_id);
      // TODO: Curse dynamic field names at query time: title_txt needs mapping.  edismax to the rescue?
      rd.fields.put("title", hit.get("title_txt"));
      double d_score = (double) hit.get("score");
      rd.score = (float) d_score;
      if (rd.score > response.max_score) response.max_score = rd.score;
      rd.explain = (String) hit.get("explain");
//      rd.explain = prettyScoreDetails(0, atlas_doc.getDocument("scoreDetails"));
      //     rd.fields.put("score_details", atlas_doc.get("scoreDetails"));

      response.documents.add(rd);
    }

//    response.print();

    response.parsed_query = "TBD: Need to change data structure that comes back from solr_select";
    return response;
  }

  @Override
  public List<DocumentAvatar> getDocuments() {
    return documents;
  }

//  @Override
//  public List<SearchRequest> getQueries() {
//    return queries;
//  }

  // thank you chatgpt:
  //      Prompt: How can I convert a Map<String,String> of parameters to a query string String for an http GET request?
  //      Output: this code below, I renamed the method, but otherwise spot on!
  public static String map_to_query_string(Map<String, String> parameters) throws UnsupportedEncodingException {
    StringBuilder queryBuilder = new StringBuilder();

    for (Map.Entry<String, String> entry : parameters.entrySet()) {
      String key = entry.getKey();
      String value = entry.getValue();

      // URL encode the key and value
      String encodedKey = URLEncoder.encode(key, "UTF-8");
      String encodedValue = URLEncoder.encode(value, "UTF-8");

      if (queryBuilder.length() > 0) {
        queryBuilder.append('&');
      }

      queryBuilder.append(encodedKey).append('=').append(encodedValue);
    }

    return queryBuilder.toString();
  }
}
