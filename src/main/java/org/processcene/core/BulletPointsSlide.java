package org.processcene.core;

import processing.core.PImage;

public class BulletPointsSlide extends BaseSlide {
  private final String[] bullets;
  private final PImage bullet_image;
  private final PImage main_image;
  private final int background_color;

  public BulletPointsSlide(String title, String[] bullets, PImage bullet_image, PImage main_image) {
    this(title, 255, bullets, bullet_image, main_image);
  }

  public BulletPointsSlide(String title, int background_color, String[] bullets, PImage bullet_image, PImage main_image) {
    super(title);

    this.background_color = background_color;
    this.bullets = bullets;
    this.bullet_image = bullet_image;
    this.main_image = main_image;
  }

  @Override
  public void draw(ProcessCene p, int step) {
    p.background(background_color);
    if (main_image != null) {
      p.image(main_image, 10, 10);
    }

    float x = 400;
    float y = 100;

    for (String bullet_text : bullets) {
      p.image(bullet_image, x, y);
      p.text(bullet_text, x + bullet_image.width + 15, y + (bullet_image.height + p.textAscent()) / 2);

      y += p.textAscent() + p.textDescent() + 25;
    }

    p.textSize(30);
    p.text(getTitle(), (p.width - p.textWidth(getTitle())) / 2, 50);

    super.draw(p, step);
  }
}
