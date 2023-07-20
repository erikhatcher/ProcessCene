import processing.core.PImage;

public class BulletPointsSlide extends BaseSlide {
  private final String[] bullets;
  private final PImage bullet_image;
  private final PImage main_image;

  public BulletPointsSlide(String title, String[] bullets, PImage bullet_image, PImage main_image, ProcessCene presentation) {
    super(title, presentation);

    this.bullets = bullets;
    this.bullet_image = bullet_image;
    this.main_image = main_image;
  }

  @Override
  public void draw(int step) {
    if (main_image != null) {
      presentation.image(main_image, 10, 10);
    }

    float x = 400;
    float y = 100;

    for (String bullet_text : bullets) {
      presentation.image(bullet_image, x, y);
      presentation.text(bullet_text, x + bullet_image.width + 15, y + (bullet_image.height + presentation.textAscent())/2);

      y += presentation.textAscent() + presentation.textDescent() + 25;
    }

    super.draw(step);
  }
}
