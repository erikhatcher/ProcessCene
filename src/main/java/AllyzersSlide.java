import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllyzersSlide extends BaseSlide {
  private final String text;
//  private final TextAnalyzer[] text_analyzers = new TextAnalyzer[] {
//      new LuceneAnalyzer(), new SolrAnalyzer()
//  };
//  private int text_analyzer_index = 0;

  private final TextAnalyzer text_analyzer;

  public AllyzersSlide(TextAnalyzer text_analyzer, String text, ProcessCene presentation) {
    super("Allyzers", presentation);

    this.text_analyzer = text_analyzer;
    this.text = text;
  }

  @Override
  public void draw(int step) {
    int x;
    int y = 20;

    for (String analyzer_name : text_analyzer.getAnalyzerNames()) {
      float last_token_width = 50;
      float x_offset = 0;
      float y_offset = 0;
      float max_y_offset = 0;

      x = 10;

      List<HashMap<String, Object>> tokens = text_analyzer.analyzeString(analyzer_name, text);

      presentation.fill(0, 0, 0);
      presentation.textSize(14);
      String label = analyzer_name + "("+ tokens.size() + "): ";
      presentation.text(label, x, y);

      int last_term_position = 0;

      Set<String> unique_terms = new HashSet<String>();
      for (HashMap<String, Object> token : tokens) {
        String term = (String) token.get("term");
        if (unique_terms.contains(term)) {
          presentation.println(analyzer_name + " repeated term: " + term);
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

        float tw = presentation.textWidth(term) + 5;
        float th = presentation.textAscent() + presentation.textDescent();
        float tx = x + x_offset; // + applet.random(3);  // The wiggling is cute though - TODO: find a way to toggle wiggle mode, perhaps through the animation flag?
        float ty = y + 20 + y_offset; // + applet.random(3);

        presentation.fill(0x00FF00);
        presentation.rect(tx, ty - th, tw, th + presentation.textDescent(), 10);

        presentation.fill(0x000000);
        presentation.text(term, tx, ty);

        last_token_width = presentation.textWidth(term) + 20;
      }

      y = (int) (y + max_y_offset + 50);
    }

    super.draw(step);
  }
}
