package org.processcene.core;

import processing.core.PImage;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class TableOfContentsSlide extends BaseSlide {
  private final List<Slide> slides;
  private int current_slide_index = -1; // main ToC, don't highlight any

  PImage bullet_image;

  public TableOfContentsSlide(List<Slide> slides, PImage bullet_image) {
    super("Table of Contents");

    this.slides = new ArrayList<>(slides);
    this.bullet_image = bullet_image;
    setShowOnTOC(false);
  }

  public TableOfContentsSlide(List<Slide> slides, PImage bullet_image, int current_slide_index) {
    this(slides, bullet_image);
    this.current_slide_index = current_slide_index;
    title = ((current_slide_index > -1) ? slides.get(current_slide_index).getTitle() : "Table Of Contents: ");
  }

  @Override
  public void mouseClicked(MouseEvent event) {
    // TODO: create array of rectangles/objects and test whether mouse coordinates are in one and then go there

    // presentation.setSlide(3);
  }

  @Override
  public void draw(ProcessCene p, int step) {
    int x = 50;
    int y = 50;

    for (int i = 0; i < slides.size(); i++) {
      Slide slide = slides.get(i);

      if (slide.getShowOnTOC()) {
        String title = slide.getTitle();
        if (title != null) {
          p.image(bullet_image, x, y);

          int fill = p.theme.foreground;
          if (current_slide_index != -1) {
            if (i < current_slide_index) {
              fill = p.theme.color_by_name("spring_green");
            } else {
              fill = p.theme.color_by_name("evergreen");
            }
          }
          p.fill(fill);
          p.text(title, x + bullet_image.width, y + bullet_image.height - p.textAscent());

          if (current_slide_index == i) {
            p.fill(p.theme.color_by_name("spring_green"), 100);
            p.rect(x + bullet_image.width - 5,
                y - 5 + bullet_image.height - 2 * p.textAscent(),
                p.textWidth(title) + 10,
                p.textAscent() + p.textDescent() + 5, 10);
          }

          y = y + bullet_image.height + 10;
        }
      }
    }

    super.draw(p, step);
  }
}
