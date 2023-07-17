import processing.event.KeyEvent;

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

  @Override
  public void keyTyped(KeyEvent event) {
    int current_step = presentation.step;
    if (event.isAltDown() && event.isControlDown()) {
      switch (event.getKey()) {
        case ',':
          if (current_step > 0) {
            current_step--;
            reset();
          }
          break;

        case '.':
          if (current_step <= getNumberOfSteps()) {
            current_step++;
            reset();
          }
          break;
      }
    }
  }

  private void reset() {
    presentation.step = 0;
  }

}
