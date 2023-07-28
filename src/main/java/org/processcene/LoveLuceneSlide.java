package org.processcene;

import processing.core.PShape;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static processing.core.PConstants.CLOSE;

public final class LoveLuceneSlide extends BaseSlide {
  private final PShape lucene_logo;

  private final int MIN_HEART_SIZE = 3;
  private final int MAX_HEART_SIZE = 20;
  private int current_heart_size = MIN_HEART_SIZE;
  private boolean heart_growing = true;

  private final Map<Integer, PShape> hearts = new HashMap<>();

  public LoveLuceneSlide(ProcessCene presentation) {
    super(null, presentation);
    setShowOnTOC(false);

    lucene_logo = presentation.loadShape(presentation.getFilePathFromResources("lucene_logo_retro.svg"));

    for (int size = MIN_HEART_SIZE; size <= MAX_HEART_SIZE; size++) {
      PShape heart = presentation.createShape();
      heart.beginShape();
      heart.fill(presentation.lavender);
      heart.stroke(presentation.spring_green);
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
  public void draw(int step) {
    if (heart_growing) {
      current_heart_size++;
      if (current_heart_size >= MAX_HEART_SIZE) heart_growing = false;
    } else {
      current_heart_size--;
      if (current_heart_size <= MIN_HEART_SIZE) heart_growing = true;
    }

    presentation.translate(presentation.width / 2, presentation.height / 2);
    presentation.shape(hearts.get(current_heart_size), 0, 0);

    if (lucene_logo != null) {
      // TODO: scale logo as the heart grows and shrinks
      //lucene_logo.scale(heart_growing ? 1.1f : 0.9f);
      presentation.shape(lucene_logo, -1 * lucene_logo.width / 2, 0 - lucene_logo.height);
    }

    super.draw(step);
  }
}
