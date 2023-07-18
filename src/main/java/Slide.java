import processing.event.KeyEvent;
import processing.event.MouseEvent;

public interface Slide {
  void draw(int step);
  void keyTyped(KeyEvent event);
  void mouseClicked(MouseEvent event);
  String getTitle();
  int getCurrentStep();
  int getCurrentVariation();
  int getNumberOfVariations();
  int getNumberOfSteps();
  boolean getShowOnTOC();

  void setVariation(int i);
  void setStep(int i);
}
