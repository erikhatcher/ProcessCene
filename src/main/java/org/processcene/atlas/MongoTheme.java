package org.processcene.atlas;

import org.processcene.core.ProcessCene;
import org.processcene.core.Theme;

import java.awt.*;

public class MongoTheme extends Theme {
  public MongoTheme() {
    // Primary Colors
    color_map.put("spring_green", Color.decode("#00ED64").getRGB());
    color_map.put("forest_green", Color.decode("#00684A").getRGB());
    color_map.put("evergreen", Color.decode("#023430").getRGB());

    // Secondary Colors
    color_map.put("mist", Color.decode("#E3FCF7").getRGB());
    color_map.put("lavender", Color.decode("#F9EBFF").getRGB());
  }

  @Override
  public void init(ProcessCene p) {
    super.init(p);

    bullet_image = p.loadImage(ProcessCene.getFilePathFromResources("Assets/normal/General_ACTION_Favorite_Inverted10x.png"));
    bullet_image.resize(50, 0);

    footer_logo = p.loadImage(ProcessCene.getFilePathFromResources("mongodb-assets/MongoDB_Logomark-SpringGreen/MongoDB_Logo.png"));
    footer_logo.resize(0, 30);

    for (int i = 0; i < 10; i++) {
      number_images[i] = p.loadImage(p.getFilePathFromResources("Assets/normal/" + i + "_10x.png"));
      number_images[i].resize(30, 30);
      number_images_inverted[i] = p.loadImage(p.getFilePathFromResources("Assets/normal/" + i + "_Inverted10x.png"));
      number_images_inverted[i].resize(30, 30);
    }

    p.textFont(p.loadFont(ProcessCene.getFilePathFromResources("LexendDeca-Light-24.vlw")));
  }

//    PImage mdb_logo = loadImage(getFilePathFromResources("mongodb-assets/MongoDB_Spring-Green/MongoDB_SpringGreen.png"));
//    mdb_logo.resize(0,300);


//    atlas_search_logo.resize(300, 0);
//    PImage atlas_big_picture = loadImage(getFilePathFromResources("atlas_big_picture.png"));
//    atlas_big_picture.resize(0, 600);
//    PImage atlas_search_ui_1 = loadImage(getFilePathFromResources("atlas_search_ui_1.png"));
//    atlas_search_ui_1.resize(0, 600);
//    PImage atlas_search_ui_2 = loadImage(getFilePathFromResources("atlas_search_ui_2.png"));
//    atlas_search_ui_2.resize(0, 700);
//    PImage atlas_search_ui_3 = loadImage(getFilePathFromResources("atlas_search_ui_3.png"));
//    atlas_search_ui_3.resize(0, 650);
//    PImage atlas_search_ui_4 = loadImage(getFilePathFromResources("atlas_search_ui_4.png"));
//    atlas_search_ui_4.resize(0, 650);
//    PImage atlas_search_ui_5 = loadImage(getFilePathFromResources("atlas_search_ui_5.png"));
//    atlas_search_ui_5.resize(0, 650);
//    PImage atlas_search_ui_6 = loadImage(getFilePathFromResources("atlas_search_ui_6.png"));
//    atlas_search_ui_6.resize(0, 600);
//    PImage atlas_search_ui_7 = loadImage(getFilePathFromResources("atlas_search_ui_7.png"));
//    atlas_search_ui_7.resize(0, 600);
//    PImage atlas_search_ui_8 = loadImage(getFilePathFromResources("atlas_search_ui_8.png"));
//    atlas_search_ui_8.resize(0, 650);
//    PImage atlas_search_ui_9 = loadImage(getFilePathFromResources("atlas_search_ui_9.png"));
//    atlas_search_ui_9.resize(0, 600);
//    PImage atlas_search_ui_10 = loadImage(getFilePathFromResources("atlas_search_ui_10.png"));
//    atlas_search_ui_10.resize(0, 600);
//    PImage atlas_search_ui_11 = loadImage(getFilePathFromResources("atlas_search_ui_11.png"));
//    atlas_search_ui_11.resize(0, 650);

//  public MongoTheme() {
////
////
//
////
////    PImage bullet_image = loadImage(getFilePathFromResources("Assets/normal/Technical_MDB_WildcardIndex10x.png"));
////    bullet_image.resize(30, 0);
////
//
//  }

}
