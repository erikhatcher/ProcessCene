package org.processcene.core;

import processing.core.PImage;

public class Theme {
  public int foreground = 0;
  public int background = 255;
  public PImage footer_logo;

  public PImage bullet_image;

  public void init(ProcessCene p) {
    // TODO: use a more generic bullet graphic, but for now the heart is good!
    bullet_image = p.loadImage(ProcessCene.getFilePathFromResources("Assets/normal/General_ACTION_Favorite_Inverted10x.png"));
    bullet_image.resize(50, 0);
  }
}
