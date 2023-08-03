package org.processcene.solr;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.processcene.core.BaseSlide;
import org.processcene.core.ProcessCene;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * See https://solr.apache.org/guide/8_7/the-tagger-handler.html
 */
public class SolrTaggerSlide extends BaseSlide {
  private PImage solr_logo;
  private List<String> texts = new ArrayList<>();
  private List<JSONObject> tagger_responses = new ArrayList<>();

  public SolrTaggerSlide(String title) {
    super(title);

    texts.add("what drama movies star keanu reeves");
//    texts.add("what drama and romance movies star keanu reeves?");
//    texts.add("tell me about harrison ford");

    SolrTagger tagger = new SolrTagger();

    for (int i = 0; i < texts.size(); i++) {
      tagger_responses.add(tagger.tag(texts.get(i)));
    }
  }

  @Override
  public void init(ProcessCene p) {
    super.init(p);
    solr_logo = p.loadImage(p.getFilePathFromResources("Solr_Logo_on_white.png"));
    solr_logo.resize(0, 150);
  }

  @Override
  public void draw(ProcessCene p, int step) {
    p.image(solr_logo, 10, 10);

    String text_to_tag = texts.get(getCurrentVariationIndex());
    JSONObject tagger_response = tagger_responses.get(getCurrentVariationIndex());
    String error_message = (String) tagger_response.get("error");

    p.textSize(20);

    float x = 200;
    float y = 250 + p.textAscent() + p.textDescent();
    ;

    p.textSize(30);
    p.text(texts.get(getCurrentVariationIndex()), x, y);

    if (error_message != null) {
      p.text(error_message, x, y + p.textAscent() + p.textDescent());
      return;
    }

    JSONArray tags = (JSONArray) tagger_response.get("tags");
    JSONArray documents = (JSONArray) ((JSONObject) tagger_response.get("response")).get("docs");

    // Build an `id` keyed map
    Map<String, JSONObject> entities = new HashMap<>();
    for (int i = 0; i < documents.size(); i++) {
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
        float before_width = p.textWidth(before_text);
        float tag_width = p.textWidth(tagged_text);

        for (int j = 0; j < ids.size(); j++) {
          String id = (String) ids.get(j);
          JSONObject doc = entities.get(id);
          String type = (String) doc.get("type");
          int tag_color = p.theme.color_by_name("spring_green");
          if ("genre".equals(type)) tag_color = p.theme.color_by_name("mist");
          if ("cast".equals(type)) tag_color = p.theme.color_by_name("evergreen");
          if ("city".equals(type)) tag_color = p.theme.color_by_name("lavender");
          p.fill(tag_color, 100);
          p.rect(x + before_width, y - p.textAscent(), tag_width, p.textAscent() + p.textDescent());
          p.fill(p.theme.foreground);
          p.text(id, x + before_width, y + (j + 1) * (p.textAscent() + p.textDescent()));

          if (render_just_this_tag) {
            int tag_x = 30 + j * 300;
            int tag_y = 350;
            p.text(id, tag_x, tag_y + (p.textAscent() + p.textDescent()));
            p.text((String) doc.get("type"), tag_x, tag_y + 2 * (p.textAscent() + p.textDescent()));
            p.text(((JSONArray) doc.get("name")).toString(), tag_x, tag_y + 3 * (p.textAscent() + p.textDescent()));

            List<String> field_names = doc.keySet().stream().toList();
            int additional_fn_shown = 0;
            for (int fi = 0; fi < field_names.size(); fi++) {
              String fn = field_names.get(fi);
              if (!(fn.equals("id") || fn.equals("type") || fn.equals("name") || fn.equals("_version_"))) {
                p.text(fn + ": " + doc.get(fn), tag_x, tag_y + (additional_fn_shown + 4) * (p.textAscent() + p.textDescent()));
                additional_fn_shown++;
              }
            }
          }
        }
      }
    }
    super.draw(p, step);
  }

  @Override
  public int getNumberOfSteps() {
    JSONObject tagger_response = tagger_responses.get(getCurrentVariationIndex());
    JSONArray tags = (JSONArray) tagger_response.get("tags");
    return (tags == null) ? 0 : tags.size();
  }

  @Override
  public int getNumberOfVariations() {
    return texts.size();
  }
}
