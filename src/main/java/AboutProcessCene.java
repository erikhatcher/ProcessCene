import processing.core.PConstants;
import processing.core.PImage;

import processing.core.PImage;
import processing.core.PShape;

public class AboutProcessCene extends BaseSlide {

  private final PImage flower_image;
  private final PImage lucene_logo;
  private final PImage processing_logo;

  public AboutProcessCene(String title, ProcessCene presentation) {
    super(title, presentation);

    flower_image = presentation.loadImage(presentation.getFilePathFromResources("Assets/normal/General_MISC_Grow10x.png"));
    flower_image.resize(0,200);

    lucene_logo = presentation.loadImage(presentation.getFilePathFromResources("lucene_green_300.png"));
    lucene_logo.resize(0,100);

    processing_logo = presentation.loadImage(presentation.getFilePathFromResources("processing_logo.png"));
    processing_logo.resize(0, 100);
  }

  @Override
  public void draw(int step) {
    presentation.background(presentation.lavender);

    String caption = "ProcessCene";
    float caption_y = presentation.textAscent() + presentation.textDescent() + presentation.height/2;
    presentation.text(caption, (presentation.width - presentation.textWidth(caption))/2, caption_y);

    float flower_scale = presentation.frameCount % 200;
    presentation.image(flower_image, (presentation.width - flower_image.width) / 2 - flower_scale, caption_y - flower_image.height - 50 - flower_scale, flower_image.width + flower_scale, flower_image.height + flower_scale);

    presentation.image(lucene_logo, (presentation.width/2) - lucene_logo.width - 50,caption_y + 20);
    presentation.image(processing_logo, (presentation.width/2) + processing_logo.width + 50,caption_y + 20);
    super.draw(step);
  }
}
