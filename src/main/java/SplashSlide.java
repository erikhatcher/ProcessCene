import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;

public class SplashSlide extends BaseSlide {
  private final int background_color;
  private final PImage image;

  private final PShape shape;

  public SplashSlide(String title, int background_color, PShape shape, ProcessCene presentation) {
    super(title, presentation);
    this.background_color = background_color;
    this.image = null;
    this.shape = shape;
  }


  public SplashSlide(String title, int background_color, PImage image, ProcessCene presentation) {
    super(title, presentation);
    this.background_color = background_color;
    this.image = image;
    this.shape = null;
  }

  @Override
  public void draw(int step) {
    presentation.background(background_color);

    if (image != null) {
      presentation.image(image, (presentation.width - image.width) / 2, (presentation.height - image.height) / 2);
    }

    // TODO: Fix shape drawing
    if (shape != null) {
      //applet.fill(125);
      presentation.shape(shape, (presentation.width - shape.width) / 2, (presentation.height - shape.height) / 2);
    }

// heart
//    applet.smooth();
//    applet.fill(255,0,0);
//    applet.beginShape();
//    applet.vertex(50, 15);
//    applet.bezierVertex(50, -5, 90, 5, 50, 40);
//    applet.vertex(50, 15);
//    applet.bezierVertex(50, -5, 10, 5, 50, 40);
//    applet.endShape();

    super.draw(step);
  }
}
