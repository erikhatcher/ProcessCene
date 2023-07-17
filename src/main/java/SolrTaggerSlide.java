import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import processing.event.MouseEvent;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * See https://solr.apache.org/guide/8_7/the-tagger-handler.html
 *
 * TODO: Re-load geonames data, mapping in other fields.
 */
public class SolrTaggerSlide extends BaseSlide {
  private final String text_to_tag = "Blake's f150 needs one of dem ph8 from the Canon City store";
  private JSONObject tagger_response;

  public SolrTaggerSlide(String title, ProcessCene presentation) {
    super(title, presentation);

    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8983/solr/tagger/tag" +
                           "?overlaps=NO_SUB&tagsLimit=5000" +
                           "&fl=*&wt=json&indent=on&echoParams=all"))
        .header("Content-Type", "text/plain")
        .POST(HttpRequest.BodyPublishers.ofString(text_to_tag))
        .build();
    try {
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      String response_body = response.body();
      System.out.println("response_body = " + response_body);
      tagger_response = (JSONObject) new JSONParser().parse(response_body);
    } catch (Exception e) {
      tagger_response = new JSONObject();
      tagger_response.put("Error", e.getLocalizedMessage());
    }
  }

  @Override
  public void draw(int step) {
    presentation.textSize(20);

    float x = 0;
    float y = presentation.textAscent() + presentation.textDescent();;

    presentation.text("Tagging:",x,y);

    x += 50;
    y += 50 + presentation.textAscent() + presentation.textDescent();;
    presentation.text(text_to_tag, x, y);

    JSONArray tags = (JSONArray) tagger_response.get("tags");
    JSONArray documents = (JSONArray) ((JSONObject) tagger_response.get("response")).get("docs");

    // Build an `id` keyed map
    Map<String,JSONObject> entities = new HashMap<>();
    for (int i=0; i < documents.size(); i++) {
      JSONObject doc = (JSONObject) documents.get(i);
      String id = (String) doc.get("id");
      entities.put(id, doc);
    }

    for (Object item : tags) {
      JSONObject tag = (JSONObject) item;
      long start_offset = (long) tag.get("startOffset");
      long end_offset = (long) tag.get("endOffset");
      JSONArray ids = (JSONArray) tag.get("ids");

      String before_text = text_to_tag.substring(0, (int) start_offset);
      String tagged_text = text_to_tag.substring((int) start_offset, (int) end_offset);
      float before_width = presentation.textWidth(before_text);
      float tag_width = presentation.textWidth(tagged_text);

      for (Object o : ids) {
        String id = (String) o;
        JSONObject doc = entities.get(id);
        String type = (String) doc.get("type");
        int tag_color = presentation.spring_green;
        if ("person".equals(type)) tag_color = presentation.mist;
        if ("vehicle".equals(type)) tag_color = presentation.evergreen;
        if ("part".equals(type)) tag_color = presentation.lavender;
        presentation.fill(tag_color, 100);
        presentation.rect(x + before_width, y - presentation.textAscent(), tag_width, presentation.textAscent() + presentation.textDescent());
        presentation.fill(presentation.black);
        presentation.text(id, x + before_width, y + presentation.textAscent() + presentation.textDescent());
      }
    }
    super.draw(step);
  }

  @Override
  public int getNumberOfSteps() {
    return ((JSONArray)tagger_response.get("tags")).size();
  }

  @Override
  public void mouseClicked(MouseEvent event) {
    super.mouseClicked(event);
  }
}
