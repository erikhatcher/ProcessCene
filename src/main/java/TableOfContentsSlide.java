import processing.core.PImage;
import processing.event.MouseEvent;

import java.util.List;

public class TableOfContentsSlide extends BaseSlide {
  private final List<Slide> slides;
  private int current_slide_index = -1; // main ToC, don't highlight any

  PImage bullet_image;

  public TableOfContentsSlide(List<Slide> slides, PImage bullet_image, ProcessCene presentation) {
    super("Table of Contents", presentation);

    this.slides = slides;
    this.bullet_image = bullet_image;
    setShowOnTOC(false);
  }

  public TableOfContentsSlide(List<Slide> slides, PImage bullet_image, int current_slide_index, ProcessCene presentation) {
    this(slides, bullet_image, presentation);
    this.current_slide_index = current_slide_index;
    title =  ((current_slide_index > -1) ? slides.get(current_slide_index).getTitle(): "Table Of Contents: ");
  }

  @Override
  public void mouseClicked(MouseEvent event) {
    // TODO: create array of rectangles/objects and test whether mouse coordinates are in one and then go there

    // presentation.setSlide(3);
  }

  @Override
  public void draw(int step) {
    int x = 50;
    int y = 50;

    for (int i = 0; i < slides.size(); i++) {
      Slide slide = slides.get(i);

      if (slide.getShowOnTOC()) {
        String title = slide.getTitle();
        if (title != null) {
          presentation.image(bullet_image, x, y);

          int fill = presentation.black;
          if (current_slide_index != -1) {
            if (i < current_slide_index) {
              fill = presentation.spring_green;
            } else {
              fill = presentation.evergreen;
            }
          }
          presentation.fill(fill);
          presentation.text(title, x + bullet_image.width, y + bullet_image.height - presentation.textAscent());

          if (current_slide_index == i) {
            presentation.fill(presentation.spring_green, 100);
            presentation.rect(x + bullet_image.width - 5, y - 5 + bullet_image.height - 2 * presentation.textAscent(), presentation.textWidth(title) + 10, presentation.textAscent() + presentation.textDescent() + 5, 10);
          }

          y = y + bullet_image.height + 10;
        }
      }
    }

    super.draw(step);
  }
}
