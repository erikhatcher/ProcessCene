package org.processcene.core;

import processing.core.PImage;

public class SplashSlide extends BaseSlide {
  private final int background_color;
  private final PImage images[];

  private String caption;

  public SplashSlide(String title, int background_color, PImage image) {
    super(title);
    this.background_color = background_color;
    this.images = (image == null) ? null : new PImage[]{image};
  }

  public SplashSlide(String title, int background_color, PImage image, String caption) {
    this(title, background_color, image);
    this.caption = caption;
  }

  public SplashSlide(String title, int background_color, PImage[] images, String caption) {
    super(title);
    this.background_color = background_color;
    this.caption = caption;
    this.images = images;
  }

  @Override
  public void draw(ProcessCene p, int step) {
    p.background(background_color);

    float caption_y = p.textAscent() + p.textDescent() + p.height / 2;
    if (images != null) {
      PImage image = images[getCurrentStep()];
      p.image(image, (p.width - image.width) / 2, (p.height - image.height) / 2 - 50);
      caption_y += image.height / 2;
    }

    if (caption != null) {
      p.text(caption, (p.width - p.textWidth(caption)) / 2, caption_y);
    }

    super.draw(p, step);
  }

  @Override
  public int getNumberOfSteps() {
    return (images == null) ? 0 : images.length - 1;
  }
}
