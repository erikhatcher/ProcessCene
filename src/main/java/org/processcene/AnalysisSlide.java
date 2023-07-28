package org.processcene;

import java.util.List;
import java.util.Map;

public class AnalysisSlide extends BaseSlide {
  // TODO: Consider making "org.processcene.TextAnalyzer base slide that has these like org.processcene.AllyzersSlide does
  private final String text;
  private final TextAnalyzer text_analyzer;

  public AnalysisSlide(String title, TextAnalyzer text_analyzer, String text, ProcessCene presentation) {
    super(title, presentation);

    this.text = text;
    this.text_analyzer = text_analyzer;
  }

  // TODO: have this report back total number of steps across ALL analyzers, so that animation can cycle through them all
  @Override
  public void draw(int step) {
    presentation.textSize(40);
    String analyzer_name = text_analyzer.getAnalyzerNames().get(getCurrentVariationIndex());
    List<Map<String, Object>> tokens = text_analyzer.analyzeString(analyzer_name, text);

    presentation.text("Analyzer: " + analyzer_name + ((step > 0) ? "    Term: " + step + "/" + getNumberOfSteps() : ""),
        10, 10 + presentation.textAscent() + presentation.textDescent());

    float text_x = (presentation.width - presentation.textWidth(text)) / 2;
    float text_y = presentation.height / 2;
    presentation.text(text, text_x, text_y);

    if (step > 0 && step < tokens.size() + 1) {
      Map<String, Object> token = tokens.get(step - 1);
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

      presentation.fill(presentation.spring_green, 150);
      presentation.rect(text_x + before_width, text_y - presentation.textAscent() - presentation.textDescent(),
          token_width, presentation.textAscent() + presentation.textDescent() + 5);

      presentation.fill(presentation.forest_green);
      float term_info_x = presentation.width / 2;
      float term_info_y = text_y + 3 * (presentation.textAscent());
      presentation.text(term, term_info_x, term_info_y);
      presentation.text("Type: " + type, term_info_x, term_info_y + 1 * (presentation.textAscent() + presentation.textDescent()));
      presentation.text("Position increment: " + position_increment, term_info_x, term_info_y + 2 * (presentation.textAscent() + presentation.textDescent()));
      presentation.text("Position length: " + position_length, term_info_x, term_info_y + 3 * (presentation.textAscent() + presentation.textDescent()));
      presentation.text("Term frequency: " + term_frequency, term_info_x, term_info_y + 4 * (presentation.textAscent() + presentation.textDescent()));
    }

    super.draw(step);
  }

  @Override
  public int getNumberOfSteps() {
    // TODO: save analyzed tokens in constuctor for each analyzer name
    String analyzer_name = text_analyzer.getAnalyzerNames().get(getCurrentVariationIndex());
    return text_analyzer.analyzeString(analyzer_name, text).size();
  }

  @Override
  public int getNumberOfVariations() {
    return text_analyzer.getAnalyzerNames().size();
  }
}
