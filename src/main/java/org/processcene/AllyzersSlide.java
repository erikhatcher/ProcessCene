package org.processcene;

import org.processcene.core.BaseSlide;
import org.processcene.core.ProcessCene;
import processing.core.PImage;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AllyzersSlide extends BaseSlide {
  private final String text;
//  private final org.processcene.TextAnalyzer[] text_analyzers = new org.processcene.TextAnalyzer[] {
//      new org.processcene.lucene.LuceneAnalyzer(), new org.processcene.solr.SolrAnalyzer()
//  };
//  private int text_analyzer_index = 0;

  private final TextAnalyzer text_analyzer;
  private PImage step_0_image;

  public AllyzersSlide(String title, TextAnalyzer text_analyzer, String text) {
    super(title);

    this.text_analyzer = text_analyzer;
    this.text = text;
  }

  @Override
  public void init(ProcessCene p) {
    super.init(p);
    step_0_image = p.loadImage(p.getFilePathFromResources("Assets/normal/Technical_ENTERPRISEADVANCED_EnterpriseServer10x.png"));
    step_0_image.resize(0, 300);
  }

  @Override
  public void draw(ProcessCene p, int step) {
    if (step == 0) {
      p.image(step_0_image, (p.width - step_0_image.width) / 2, (p.height - step_0_image.height) / 2);
    }


    int x;
    int y = 20;

    List<String> analyzer_names = text_analyzer.getAnalyzerNames();
    for (int i = 0; i < step; i++) {

      String analyzer_name = analyzer_names.get(i);

      float last_token_width = 50;
      float x_offset = 0;
      float y_offset = 0;
      float max_y_offset = 0;

      x = 10;

      List<Map<String, Object>> tokens = text_analyzer.analyzeString(analyzer_name, text);

//      p.fill(0, 0, 0);
      p.textSize(14);
      String label = analyzer_name + " (" + tokens.size() + "): ";
      p.text(label, x, y);

      int last_term_position = 0;

      Set<String> unique_terms = new HashSet<String>();
      for (Map<String, Object> token : tokens) {
        String term = (String) token.get("term");
        if (unique_terms.contains(term)) {
          p.println(analyzer_name + " repeated term: " + term);
        }
        unique_terms.add(term);
        int current_term_position = last_term_position + (int) token.get("position_increment");

        if (current_term_position == last_term_position) {
          // we have an overlap
          x_offset += 10;
          y_offset += 5;
          max_y_offset += 5;
        } else {
          x = (int) (x + last_token_width);  //  + x_offset
          x_offset = 0;
          y_offset = 0;
          max_y_offset = 0;
        }

        float tw = p.textWidth(term);
        float th = p.textAscent() + p.textDescent();
        float tx = x + x_offset; // + ((step == getNumberOfSteps()) ? ThreadLocalRandom.current().nextInt(1, 3 + 1) : 0);
        float ty = y + 20 + y_offset; // + ((step == getNumberOfSteps()) ? ThreadLocalRandom.current().nextInt(1, 3 + 1) : 0);

        p.fill(p.theme.color_by_name("mist"));
        p.rect(tx - 5, ty - th, tw + 10, th + 5, 7);

        p.fill(p.theme.foreground);
        p.text(term, tx, ty);

        last_token_width = p.textWidth(term) + 20;
      }

      y = (int) (y + max_y_offset + 50);
    }

    super.draw(p, step);
  }

  @Override
  public int getNumberOfSteps() {
    return text_analyzer.getAnalyzerNames().size();
  }
}
