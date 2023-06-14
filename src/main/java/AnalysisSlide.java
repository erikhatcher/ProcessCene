import processing.core.PApplet;
import processing.event.KeyEvent;

import java.util.HashMap;
import java.util.List;

public class AnalysisSlide extends BaseSlide {
  // TODO: Consider making "TextAnalyzer base slide that has these like AllyzersSlide does
  private final String text;
  private final TextAnalyzer text_analyzer;

  private int analyzer_index = 0;
  private List<HashMap<String, Object>> tokens;

  public AnalysisSlide(TextAnalyzer text_analyzer, String text, ProcessCene presentation) {
    super("Analysis", presentation);

    this.text = text;
    this.text_analyzer = text_analyzer;

    reset();
  }

  private void reset() {
    // Given the current analyzer_index, and the fixed text and text_analyzer state,
    // reset the tokens state
    String analyzer_name = text_analyzer.getAnalyzerNames().get(analyzer_index);
    tokens = text_analyzer.analyzeString(analyzer_name, text);
  }

  @Override
  public void keyTyped(KeyEvent event) {
    if (event.isAltDown() && event.isControlDown()) {
      List<String> analyzer_names = text_analyzer.getAnalyzerNames();
      switch (event.getKey()) {
        case ',':
          if (analyzer_index > 0) {
            analyzer_index--;
            reset();
          }
          break;

        case '.':
          if (analyzer_index < (analyzer_names.size() - 1)) {
            analyzer_index++;
            reset();
          }
          break;
      }
    }
  }

  // TODO: have this report back total number of steps across ALL analyzers, so that animation can cycle through them all
  @Override
  public void draw(int step) {
    presentation.background(255,255,255);

    presentation.fill(0,0,0);
    presentation.textSize(14);
    presentation.text("Step: " + step, 10, 320);

    String analyzer_name = text_analyzer.getAnalyzerNames().get(analyzer_index);
    presentation.text("Analyzer: " + analyzer_name, 10, 340);

    presentation.textSize(20);
    presentation.text(text, 50, 50);

    presentation.fill(0,100,100,100);
    if (step > 0 && step < tokens.size() + 1) {
      HashMap<String,Object> token = tokens.get(step - 1);
      int start_offset = (int) token.get("start_offset");
      int end_offset = (int) token.get("end_offset");
      String term = (String) token.get("term");
      String type = (String) token.get("type");
      int position_increment = (int) token.get("position_increment");
      int position_length = (int) token.get("position_length");
      int term_frequency = (int) token.get("term_frequency");

      String before = text.substring(0, start_offset);
      float before_width = presentation.textWidth(before);
      float token_width = presentation.textWidth(text.substring(start_offset, end_offset));

      presentation.rect(50+before_width, 50 - presentation.textAscent() - presentation.textDescent(),
                 token_width, presentation.textAscent() + presentation.textDescent() + 5);
      presentation.text(term, 50+before_width, 100);

      presentation.text("Type: " + type, 50,200);
      presentation.text("Position increment: " + position_increment, 50,220);
      presentation.text("Position length: " + position_length, 50,240);
      presentation.text("Term frequency: " + term_frequency, 50,260);
    }

    super.draw(step);
  }

  @Override
  public int getNumberOfSteps() {
    return tokens.size();
  }
}
