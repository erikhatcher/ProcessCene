import processing.event.KeyEvent;
import processing.event.MouseEvent;

public interface Slide {
  void draw(int step);
  void keyTyped(KeyEvent event);
  void mouseClicked(MouseEvent event);
  String getTitle();
  int getNumberOfSteps();
  boolean getShowOnTOC();
}
