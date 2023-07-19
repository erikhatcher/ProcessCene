public class BulletPointsSlide extends BaseSlide {
  private final String[] bullets;

  public BulletPointsSlide(String title, String[] bullets, ProcessCene presentation) {
    super(title, presentation);

    this.bullets = bullets;
  }

  @Override
  public void draw(int step) {
    float x = 100;
    float y = 100;

    for (String bullet_text : bullets) {
      presentation.text("* " + bullet_text, x, y);

      y += presentation.textAscent() + presentation.textDescent();
    }


    super.draw(step);
  }
}
