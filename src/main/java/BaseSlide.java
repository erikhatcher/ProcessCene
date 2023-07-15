import processing.event.KeyEvent;
import processing.event.MouseEvent;

abstract public class BaseSlide implements Slide {
  protected final String title;
  protected final ProcessCene presentation;

  public BaseSlide(String title, ProcessCene presentation) {
    this.title = title;
    this.presentation = presentation;
  }

  @Override
  public void keyTyped(KeyEvent event) {
  }

  @Override
  public void mouseClicked(MouseEvent event) {
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public void draw(int step) {
    presentation.windowTitle((title != null) ? title : presentation.getClass().getName() + " " + step + "/" + getNumberOfSteps());
  }

  @Override
  public int getNumberOfSteps() {
    return 1;
  }

  @Override
  public boolean getShowOnTOC() {
    return true;
  }
}
