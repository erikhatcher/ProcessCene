package org.processcene.core;

import processing.core.PImage;

public class AboutProcessCene extends BaseSlide {

  private PImage flower_image;
  private PImage lucene_logo;
  private PImage processing_logo;

  public AboutProcessCene(String title) {
    super(title);
  }

  @Override
  public void init(ProcessCene p) {
    super.init(p);
    flower_image = p.loadImage(p.getFilePathFromResources("Assets/normal/General_MISC_Grow10x.png"));
    flower_image.resize(0, 200);

    lucene_logo = p.loadImage(p.getFilePathFromResources("lucene_green_300.png"));
    lucene_logo.resize(0, 100);

    processing_logo = p.loadImage(p.getFilePathFromResources("processing_logo.png"));
    processing_logo.resize(0, 100);

  }

  @Override
  public void draw(ProcessCene p, int step) {
    p.background(p.theme.background);

    String caption = "ProcessCene";
    float caption_y = p.textAscent() + p.textDescent() + p.height / 2;
    p.text(caption, (p.width - p.textWidth(caption)) / 2, caption_y);

    float flower_scale = p.frameCount % 400 + 2;
    p.image(flower_image, (p.width - flower_image.width) / 2 - flower_scale, caption_y - flower_image.height - 50 - flower_scale, flower_image.width + flower_scale, flower_image.height + flower_scale);

    p.image(lucene_logo, (p.width / 2) - lucene_logo.width - 50, caption_y + 20);
    p.image(processing_logo, (p.width / 2) + processing_logo.width + 50, caption_y + 20);
    super.draw(p, step);
  }
}
