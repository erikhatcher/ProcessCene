package org.processcene.solr;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.processcene.core.Tagger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class SolrTagger implements Tagger {
  private final String tagger_collection;

  public SolrTagger(String collection) {
    super();

    tagger_collection = collection;
  }

  @Override
  public JSONObject tag(String input) {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8983/solr/" + tagger_collection + "/tag" +
            "?overlaps=NO_SUB&tagsLimit=5000" +
            "&fl=*&wt=json&indent=on&echoParams=all"))
        .header("Content-Type", "text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(input))
        .build();
    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      String response_body = response.body();
      // System.out.println("response_body = " + response_body);
      return (JSONObject) new JSONParser().parse(response_body);
    } catch (Exception e) {
      JSONObject o = new JSONObject();
      o.put("error", e.toString());
      return o;
    }

  }
}
