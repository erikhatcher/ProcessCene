package org.processcene.core;

import processing.core.PImage;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class DocumentAvatar {
  public int id;
  public String type;
  public String title;
  public Map<String, Object> document;

  public boolean on = false;
  public int position;
  public float score = 0.0f;

  public int x;
  public int y;

  // coordinates constructed at
  public int home_x;
  public int home_y;
  public String explain;

  public DocumentAvatar(Map<String, Object> doc, int x, int y) {
    id = (Integer) doc.get("id");
    title = (String) doc.get("title");
    type = (String) doc.get("type");

    document = doc;
    this.x = x;
    this.y = y;
    home_x = x;
    home_y = y;
  }

  public DocumentAvatar(Map<String, Object> doc) {
    this(doc, 0, 0);
  }

  public void jiggle() {
    x += ThreadLocalRandom.current().nextInt(-2, 3);
    y += ThreadLocalRandom.current().nextInt(-2, 3);
  }

  /**
   * Draw myself in my current coordinates
   *
   * @param p
   */
  public void draw(ProcessCene p) {
    draw(p, x, y);
  }

  public void print() {
    System.out.println("Doc " + id + ": " + (on ? " ON" : "OFF") + " (" + x + "," + y + ")");
  }

  /**
   * Draw a copy of myself at the specified screen coordinates
   *
   * @param p
   * @param x
   * @param y
   */
  public void draw(ProcessCene p, float x, float y) {
    PImage doc_image;
    if (id > 9) {
      doc_image = on ? p.theme.doc_img_inverted : p.theme.doc_img;
    } else {
      doc_image = on ? p.theme.number_images_inverted[id] : p.theme.number_images[id];
    }
    p.image(doc_image, x, y);

//    p.text(title, x, y);
  }

  public void draw_as_doc(ProcessCene p) {
//    PImage doc_image = on ? p.theme.doc_img_inverted : p.theme.doc_img;
    p.push();
    PImage doc_image;
    if (position > 9 || position <= 0) {
      doc_image = on ? p.theme.doc_img_inverted : p.theme.doc_img;
    } else {
      doc_image = on ? p.theme.number_images_inverted[position] : p.theme.number_images[position];
    }
    p.image(doc_image, x, y);

    p.stroke(p.theme.color_by_name("spring_green"));
    p.strokeWeight(5.0f);
    p.noFill();

    p.pop();
    // if (on) p.circle(x+doc_image.width/2,y+doc_image.height/2, doc_image.width);
  }
}
