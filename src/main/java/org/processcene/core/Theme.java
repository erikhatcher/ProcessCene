package org.processcene.core;

import processing.core.PImage;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Theme {
  // TODO: provide additional colors in named fashion:
  //     * dark mode?
  //     * secondary_color, primary_inverted?, light_background, dark_foreground...
  public int foreground = 0;
  public int background = 255;
  public PImage footer_logo = null;

  public PImage bullet_image = null;

  public PImage[] number_images = new PImage[10];  // icons for numbers 0 - 9
  public PImage[] number_images_inverted = new PImage[10];  // icons for numbers 0 - 9
  public int error_color = Color.decode("#FF0000").getRGB();

  protected Map<String, Integer> color_map = new HashMap<>();
  public PImage doc_img;
  public PImage doc_img_inverted;

  public void init(ProcessCene p) {
    // TODO: use a more generic bullet graphic, but for now the heart is good!
    bullet_image = p.loadImage(ProcessCene.getFilePathFromResources("Assets/normal/General_ACTION_Favorite_Inverted10x.png"));
    bullet_image.resize(50, 0);

    // Copy the MDB theme for now, until we // TODO: generate a number in a graphic for these images
    for (int i = 0; i < 10; i++) {
      number_images[i] = p.loadImage(p.getFilePathFromResources("Assets/normal/" + i + "_10x.png"));
      number_images[i].resize(30, 30);
      number_images_inverted[i] = p.loadImage(p.getFilePathFromResources("Assets/normal/" + i + "_Inverted10x.png"));
      number_images_inverted[i].resize(30, 30);
    }

    doc_img = p.loadImage(ProcessCene.getFilePathFromResources("Assets/normal/General_ACTION_Read_10x.png"));
    doc_img.resize(30, 30);

    doc_img_inverted = p.loadImage(ProcessCene.getFilePathFromResources("Assets/normal/General_ACTION_Read_inverse_10x.png"));
    doc_img_inverted.resize(30, 30);
  }

  public int color_by_name(String name) {
    return color_map.get(name);
  }
}
