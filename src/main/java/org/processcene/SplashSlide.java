package org.processcene;

import processing.core.PImage;

public class SplashSlide extends BaseSlide {
  private final int background_color;
  private final PImage images[];

  private String caption;

  public SplashSlide(String title, int background_color, PImage image, ProcessCene presentation) {
    super(title, presentation);
    this.background_color = background_color;
    this.images = (image == null) ? null : new PImage[] {image};
  }

  public SplashSlide(String title, int background_color, PImage image, String caption, ProcessCene presentation) {
    this(title, background_color, image, presentation);
    this.caption = caption;
  }

  public SplashSlide(String title, int background_color, PImage[] images, String caption, ProcessCene presentation) {
    super(title, presentation);
    this.background_color = background_color;
    this.caption = caption;
    this.images = images;
  }

  @Override
  public void draw(int step) {
    presentation.background(background_color);

    float caption_y = presentation.textAscent() + presentation.textDescent() + presentation.height/2;
    if (images != null) {
      PImage image = images[getCurrentStep()];
      presentation.image(image, (presentation.width - image.width) / 2, (presentation.height - image.height) / 2 - 50);
      caption_y += image.height / 2;
    }

    if (caption != null) {
      presentation.text(caption, (presentation.width - presentation.textWidth(caption))/2, caption_y);
    }

    super.draw(step);
  }

  @Override
  public int getNumberOfSteps() {
    return (images == null) ? 0 : images.length - 1;
  }
}
