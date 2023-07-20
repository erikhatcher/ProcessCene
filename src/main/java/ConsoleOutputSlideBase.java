import processing.event.KeyEvent;

/**
 * This is a handy base class to dump text output quickly.
 * TODO: It's not currently used though.  Remove it?
 */
abstract public class ConsoleOutputSlideBase extends BaseSlide {
  public ConsoleOutputSlideBase(String title, ProcessCene presentation) {
    super(title, presentation);
  }

  @Override
  public void draw(int step) {
    String description = getDescription(step);
    String output = getOutput(step);

    float x = 0;
    float y = presentation.textAscent() + presentation.textDescent();
    presentation.text(description, x, y);
    y += presentation.textAscent() + presentation.textDescent();

    presentation.text(output, x, y);
    y += presentation.textAscent() + presentation.textDescent();

    super.draw(step);
  }

  protected abstract String getDescription(int step);
  protected abstract String getOutput(int step);
}
