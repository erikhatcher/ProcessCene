package org.processcene;

import org.processcene.core.ProcessCene;
import processing.core.PImage;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class DocumentAvatar {
  public String title;
  public String type;
  public int x;
  public int y;
  public int id;

  public Map<String,Object> document;
  public boolean on = true;
  public float score = 0.0f;

  public DocumentAvatar(Map<String,Object> doc, int x, int y) {
    id = (Integer) doc.get("id");
    title = (String) doc.get("title");
    type = (String) doc.get("type");

    document = doc;
    this.x = x;
    this.y = y;
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
   * @param p
   */
  public void draw(ProcessCene p) {
    draw(p, x, y);
  }

  public void print() {
    System.out.println("Doc " + id + ": " + (on ? " ON": "OFF") + " (" + x + "," + y + ")");
  }

  /**
   * Draw a copy of myself at the specified screen coordinates
   * @param p
   * @param x
   * @param y
   */
  public void draw(ProcessCene p, float x, float y) {
    PImage doc_image = on ? p.theme.doc_images_inverted[id] : p.theme.doc_images[id];
    p.image(doc_image, x, y);

    p.text(title, x, y);
  }
}
