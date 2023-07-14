import processing.core.PImage;
import processing.core.PShape;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;

public class UberconfTitleSlide extends BaseSlide {
  private final PShape lucene_logo;
  private int heartSize = 5;
  private boolean heartGrowing = true;

  public UberconfTitleSlide(String title, ProcessCene presentation) {
    super(title, presentation);

    // TODO: fix absolute path
    // lucene_logo = presentation.loadImage("/Users/erik.hatcher/dev/ProcessCene/src/main/resources/lucene_green_300.png");
    lucene_logo = presentation.loadShape("/Users/erik.hatcher/dev/ProcessCene/src/main/resources/lucene_logo_retro.svg");
  }

  @Override
  public void draw(int step) {
    if (heartGrowing) {
      heartSize++;
      if (heartSize >= 15) heartGrowing = false;
    } else {
      heartSize--;
      if (heartSize <= 5) heartGrowing = true;
    }

    presentation.background(255);

//    if (lucene_logo != null) {
//      presentation.image(lucene_logo, (presentation.width - lucene_logo.width) / 2, (presentation.height - lucene_logo.height) / 2);
//    }
    if (lucene_logo != null) {
      presentation.shape(lucene_logo, (presentation.width - lucene_logo.width) / 2, (presentation.height - lucene_logo.height) / 2);
    }

    presentation.translate(presentation.width / 2,presentation.height / 2);
    presentation.stroke(250, 0, 0);
    presentation.strokeWeight(4);
    for (float t=0; t<=2*PI; t+=.01) {
      presentation.point((float) (-16 * heartSize * pow(sin(t), 3)), (float) -(13 * heartSize * cos(t) - 5 * heartSize * cos(2 * t) - 2 * heartSize * cos(3 * t) - cos(4 * t)));
    }

    super.draw(step);
  }
}
