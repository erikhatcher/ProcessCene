package org.processcene.core;

import processing.core.PImage;

public class BulletPointsSlide extends BaseSlide {
  private final String[] bullets;
  private PImage bullet_image;
  private PImage main_image;

  public BulletPointsSlide(String title, String[] bullets) {
    super(title);

    this.bullets = bullets;
  }

  @Override
  public void init(ProcessCene p) {
    super.init(p);

    bullet_image = p.theme.bullet_image;
  }

  @Override
  public void draw(ProcessCene p, int step) {
    p.background(p.theme.background);  // redundant?   make this overridable?
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
