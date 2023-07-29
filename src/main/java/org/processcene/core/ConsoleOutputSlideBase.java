package org.processcene.core;

/**
 * This is a handy base class to dump text output quickly.
 * TODO: It's not currently used though.  Remove it?
 */
abstract public class ConsoleOutputSlideBase extends BaseSlide {
  public ConsoleOutputSlideBase(String title) {
    super(title);
  }

  @Override
  public void draw(ProcessCene p, int step) {
    String description = getDescription(step);
    String output = getOutput(step);

    float x = 0;
    float y = p.textAscent() + p.textDescent();
    p.text(description, x, y);
    y += p.textAscent() + p.textDescent();

    p.text(output, x, y);
    y += p.textAscent() + p.textDescent();

    super.draw(p, step);
  }

  protected abstract String getDescription(int step);

  protected abstract String getOutput(int step);
}
