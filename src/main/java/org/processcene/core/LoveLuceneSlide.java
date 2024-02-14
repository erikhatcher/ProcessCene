package org.processcene.core;

import processing.core.PShape;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static processing.core.PConstants.CLOSE;

public final class LoveLuceneSlide extends BaseSlide {
  private final boolean show_heart;
  private PShape lucene_logo;

  private final int MIN_HEART_SIZE = 3;
  private final int MAX_HEART_SIZE = 20;
  private int current_heart_size = MIN_HEART_SIZE;
  private boolean heart_growing = true;

  private final Map<Integer, PShape> hearts = new HashMap<>();

  public LoveLuceneSlide() {
    this(true);
  }

  public LoveLuceneSlide(boolean show_heart) {
    super(null);
    setShowOnTOC(false);
    this.show_heart = show_heart;
  }

  @Override
  public void init(ProcessCene p) {
    super.init(p);

    lucene_logo = p.loadShape(p.getFilePathFromResources("lucene_logo_retro.svg"));
    if (!show_heart) { lucene_logo.scale(3); }

    for (int size = MIN_HEART_SIZE; size <= MAX_HEART_SIZE; size++) {
      PShape heart = p.createShape();
      heart.beginShape();
      heart.fill(Color.decode("#F9EBFF").getRGB());  // TODO:
      heart.stroke(Color.decode("#00ED64").getRGB());
      heart.strokeWeight(5);
      for (float t = 0; t <= 2 * PI; t += .10) {
        float x = (float) (-16 * size * pow(sin(t), 3));
        float y = (float) -(13 * size * cos(t) - 5 * size * cos(2 * t) - 2 * size * cos(3 * t) - cos(4 * t));
        heart.vertex(x, y);
      }
      heart.endShape(CLOSE);

      hearts.put(size, heart);
    }
  }


  @Override
  public void draw(ProcessCene p, int step) {
    p.translate(p.width / 2, p.height / 2);

    if (show_heart) {
      if (heart_growing) {
        current_heart_size++;
        if (current_heart_size >= MAX_HEART_SIZE) heart_growing = false;
      } else {
        current_heart_size--;
        if (current_heart_size <= MIN_HEART_SIZE) heart_growing = true;
      }

      p.shape(hearts.get(current_heart_size), 0, 0);
    }

    if (lucene_logo != null) {
      float x = show_heart ? -1 * lucene_logo.width / 2 : -2 * lucene_logo.width;
      float y = show_heart ? 0 - lucene_logo.height : -4 * lucene_logo.height;
      p.shape(lucene_logo, x, y);
    }

    super.draw(p, step);
  }

}
