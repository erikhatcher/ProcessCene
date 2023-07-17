import processing.event.KeyEvent;
import processing.event.MouseEvent;

abstract public class BaseSlide implements Slide {
  protected String title;
  protected final ProcessCene presentation;
  protected boolean show_on_toc = true;

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
    presentation.windowTitle("Uberconf '23: Love of Lucene: " + ((title != null) ? title : presentation.getClass().getName() + " " + step + "/" + getNumberOfSteps()));
  }

  @Override
  public int getNumberOfSteps() {
    return 0;
  }

  @Override
  public boolean getShowOnTOC() {
    return show_on_toc;
  }

  public Slide setShowOnTOC(boolean show_on_toc) {
    this.show_on_toc = show_on_toc;
    return this;
  }
}
