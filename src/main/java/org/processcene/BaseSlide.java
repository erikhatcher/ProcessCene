package org.processcene;

import processing.event.KeyEvent;
import processing.event.MouseEvent;

abstract public class BaseSlide implements Slide {
  protected String title;
  protected final ProcessCene presentation;
  protected boolean show_on_toc = true;
  private int current_step = 0;

  /**
   * variation is 1-based, whereas step is zero-based (can show intro on step 0, though variation is always set)
   */
  private int current_variation = 1;

  public BaseSlide(String title, ProcessCene presentation) {
    this.title = title;
    this.presentation = presentation;
  }

  @Override
  public void keyTyped(KeyEvent event) {
  }

  @Override
  public void mouseClicked(MouseEvent event) {
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public void draw(int step) {
    presentation.windowTitle("Uberconf '23: Love of Lucene" + ((title != null) ? ": " + title : ""));
    // TODO: maybe render presentation.getClass().getName() if title is null + " " + step + "/" + getNumberOfSteps())
  }

  @Override
  public int getNumberOfSteps() {
    return 0;
  }

  @Override
  final public int getCurrentStep() {
    return current_step;
  }

  @Override
  final public void setStep(int i) {
    current_step = i;
  }

  @Override
  public int getNumberOfVariations() {
    return 0;
  }


  /**
   *
   * @return 1-based current variation
   */
  @Override
  public int getCurrentVariation() {
    return current_variation;
  }

  /**
   *
   * @return 0-based current variation, useful in .get'ing from 0-based array of variations
   */
  public int getCurrentVariationIndex() {
    return current_variation - 1;
  }

  @Override
  final public void setVariation(int i) {
    current_variation = i;
    setStep(0);
  }

  @Override
  final public boolean getShowOnTOC() {
    return show_on_toc;
  }

  /**
     builder-style, returning this
   */
  final public Slide setShowOnTOC(boolean show_on_toc) {
    this.show_on_toc = show_on_toc;
    return this;
  }
}
