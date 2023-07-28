package org.processcene.solr;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.processcene.BaseSlide;
import org.processcene.ProcessCene;
import processing.core.PImage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * See https://solr.apache.org/guide/8_7/the-tagger-handler.html
 */
public class SolrTaggerSlide extends BaseSlide {
  private final PImage solr_logo;
  private List<String> texts = new ArrayList<>();
  private List<JSONObject> tagger_responses = new ArrayList<>();

  public SolrTaggerSlide(String title, ProcessCene presentation) {
    super(title, presentation);

    texts.add("hey, check out that lucene presentation at üb3rc0nf in westMINISTER");
    texts.add("where can I get tested for covid?");

    for(int i=0; i < texts.size(); i++) {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create("http://localhost:8983/solr/tagger/tag" +
              "?overlaps=NO_SUB&tagsLimit=5000" +
              "&fl=*&wt=json&indent=on&echoParams=all"))
          .header("Content-Type", "text/plain")
          .POST(HttpRequest.BodyPublishers.ofString(texts.get(i)))
          .build();
      try {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String response_body = response.body();
        // System.out.println("response_body = " + response_body);
        tagger_responses.add((JSONObject) new JSONParser().parse(response_body));
      } catch (Exception e) {
        JSONObject o = new JSONObject();
        o.put("error", e.toString());
        tagger_responses.add(o);
      }
    }

    solr_logo = presentation.loadImage(presentation.getFilePathFromResources("Solr_Logo_on_white.png"));
    solr_logo.resize(0, 150);
  }

  @Override
  public void draw(int step) {
    presentation.image(solr_logo, 10, 10);

    String text_to_tag = texts.get(getCurrentVariationIndex());
    JSONObject tagger_response = tagger_responses.get(getCurrentVariationIndex());
    String error_message = (String)tagger_response.get("error");

    presentation.textSize(20);

    float x = 200;
    float y = 250 + presentation.textAscent() + presentation.textDescent();;

    presentation.textSize(30);
    presentation.text(texts.get(getCurrentVariationIndex()), x, y);

    if (error_message != null) {
      presentation.text(error_message, x, y + presentation.textAscent() + presentation.textDescent());
      return;
    }

    JSONArray tags = (JSONArray) tagger_response.get("tags");
    JSONArray documents = (JSONArray) ((JSONObject) tagger_response.get("response")).get("docs");

    // Build an `id` keyed map
    Map<String,JSONObject> entities = new HashMap<>();
    for (int i=0; i < documents.size(); i++) {
      JSONObject doc = (JSONObject) documents.get(i);
      String id = (String) doc.get("id");
      entities.put(id, doc);
    }

    for (int i = 0; i < tags.size(); i++) {
      boolean render_just_this_tag = ((step > 0) && (i == step - 1));
      if (step == 0 || render_just_this_tag) {
        JSONObject tag = (JSONObject) tags.get(i);
        long start_offset = (long) tag.get("startOffset");
        long end_offset = (long) tag.get("endOffset");
        JSONArray ids = (JSONArray) tag.get("ids");

        String before_text = text_to_tag.substring(0, (int) start_offset);
        String tagged_text = text_to_tag.substring((int) start_offset, (int) end_offset);
        float before_width = presentation.textWidth(before_text);
        float tag_width = presentation.textWidth(tagged_text);

        for (int j = 0; j < ids.size(); j++) {
          String id = (String) ids.get(j);
          JSONObject doc = entities.get(id);
          String type = (String) doc.get("type");
          int tag_color = presentation.spring_green;
          if ("person".equals(type)) tag_color = presentation.mist;
          if ("event".equals(type)) tag_color = presentation.evergreen;
          if ("city".equals(type)) tag_color = presentation.lavender;
          presentation.fill(tag_color, 100);
          presentation.rect(x + before_width, y - presentation.textAscent(), tag_width, presentation.textAscent() + presentation.textDescent());
          presentation.fill(presentation.black);
          presentation.text(id, x + before_width, y + (j + 1) * (presentation.textAscent() + presentation.textDescent()));

          if (render_just_this_tag) {
            int tag_x = 30 + j * 300;
            int tag_y = 350;
            presentation.text(id, tag_x, tag_y + (presentation.textAscent() + presentation.textDescent()));
            presentation.text((String) doc.get("type"), tag_x, tag_y + 2 * (presentation.textAscent() + presentation.textDescent()));
            presentation.text(((JSONArray) doc.get("name")).toString(), tag_x, tag_y + 3 * (presentation.textAscent() + presentation.textDescent()));

            List<String> field_names = doc.keySet().stream().toList();
            int additional_fn_shown = 0;
            for (int fi = 0; fi < field_names.size(); fi++) {
              String fn = field_names.get(fi);
              if (!(fn.equals("id") || fn.equals("type") || fn.equals("name") || fn.equals("_version_"))) {
                presentation.text(fn + ": " + doc.get(fn), tag_x, tag_y + (additional_fn_shown + 4) * (presentation.textAscent() + presentation.textDescent()));
                additional_fn_shown++;
              }
            }
          }
        }
      }
    }
    super.draw(step);
  }

  @Override
  public int getNumberOfSteps() {
    JSONObject tagger_response = tagger_responses.get(getCurrentVariationIndex());
    JSONArray tags = (JSONArray)tagger_response.get("tags");
    return (tags == null) ? 0 : tags.size();
  }

  @Override
  public int getNumberOfVariations() {
    return texts.size();
  }
}
