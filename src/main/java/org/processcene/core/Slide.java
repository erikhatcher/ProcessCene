package org.processcene.core;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

public interface Slide {
  void init(ProcessCene p);

  void keyTyped(KeyEvent event);

  void mouseClicked(MouseEvent event);

  String getTitle();

  int getCurrentStep();

  int getCurrentVariation();

  int getNumberOfVariations();

  void draw(ProcessCene p, int step);

  int getNumberOfSteps();

  boolean getShowOnTOC();

  void setVariation(int i);

  void setStep(int i);
}
