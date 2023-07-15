import processing.core.PImage;
import processing.event.MouseEvent;

import java.util.List;

public class TableOfContentsSlide extends BaseSlide {
  private final List<Slide> slides;

  PImage bullet_image;

  @Override
  public boolean getShowOnTOC() {
    return false;
  }

  public TableOfContentsSlide(List<Slide> slides, PImage bullet_image, ProcessCene presentation) {
    super("Table Of Contents", presentation);

    this.slides = slides;
    this.bullet_image = bullet_image;
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
        boolean me = false;
        if (title != null) {
          presentation.image(bullet_image, x, y);
          presentation.text(title, x + bullet_image.width, y + bullet_image.height - presentation.textAscent());

          // TODO: highlight slide title text that is being hovered on

          y = y + bullet_image.height + 10;
        }
      }
    }

    super.draw(step);
  }
}
