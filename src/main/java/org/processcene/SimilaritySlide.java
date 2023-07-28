package org.processcene;

import processing.core.PApplet;

import java.awt.*;
import java.util.List;
import java.util.function.Function;

public class SimilaritySlide extends BaseSlide {
  private static final float MIN_X = -1.0f; // (float) (-1.0f * Math.PI);
  private static final float MAX_X = 10.0f; // (float) Math.PI;
  private static final float MIN_Y = -1.0f;
  private static final float MAX_Y = 3.0f;
  private static final float START_X = 1.0f;
  private static final float interval = 0.1f;

  private final Point center = new Point(0, 0);
  private final Point left_middle = new Point(MIN_X, 0);
  private final Point right_middle = new Point(MAX_X, 0);
  private final Point bottom_middle = new Point(0, MIN_Y);
  private final Point top_middle = new Point(0, MAX_Y);
  private final Point bottom_left = new Point(MIN_X, MIN_Y);
  private final Point top_left = new Point(MIN_X, MAX_Y);
  private final Point bottom_right = new Point(MAX_X, MIN_Y);
  private final Point top_right = new Point(MAX_X, MAX_Y);
  private List<Function<Float, Float>> functions;

  public SimilaritySlide(String title, ProcessCene presentation) {
    super(title, presentation);

    presentation.println("Screen size:", presentation.width, presentation.height);
    center.print("Center");
    bottom_middle.print("bm");
    top_middle.print("tm");

//    Function<Float, Float> line_sloped = v -> Line.factory(0.2f,0.0f).f(v);
//    Function<Float, Float> line_flat = x -> Line.factory(0.0f,1.2f * 0.75f).f(x);

    Function<Float, Float> classic_tf = tf -> (float) Math.sqrt(tf);
    Function<Float, Float> bm25_tf_default = tf -> BM25.factory(1.2f, 0.75f, 50.0f, 75.0f).f(tf);
    Function<Float, Float> bm25_tf_1 = tf -> BM25.factory(0.0f, 0.75f, 50.0f, 75.0f).f(tf);
    Function<Float, Float> bm25_tf_2 = tf -> BM25.factory(1.2f, 0.0f, 50.0f, 75.0f).f(tf);
    Function<Float, Float> bm25_tf_3 = tf -> BM25.factory(0.0f, 0.0f, 50.0f, 75.0f).f(tf);
    Function<Float, Float> bm25_tf_4 = tf -> BM25.factory(5.0f, 3.0f, 50.0f, 75.0f).f(tf);
    Function<Float, Float> bm25_tf_5 = tf -> BM25.factory(0.1f, 0.2f, 50.0f, 75.0f).f(tf);
    Function<Float, Float> bm25_tf_6 = tf -> BM25.factory(0.00001f, 0.00001f, 50.0f, 75.0f).f(tf);

    functions = List.of(classic_tf, bm25_tf_default, bm25_tf_1, bm25_tf_2,
        bm25_tf_3, bm25_tf_4, bm25_tf_5, bm25_tf_6);
  }

  private float function_classic_idf(long doc_freq, long doc_count) {
    return (float) (Math.log((doc_count + 1) / (double) (doc_freq + 1)) + 1.0);
  }

  //  private float function_bm25_idf(long doc_freq, long doc_count) {
//    return (float) Math.log(1 + (doc_count - doc_freq + 0.5D) / (doc_freq + 0.5D));
//  }
//
//  private float function_classic_tf(float tf) {
//    return (float) Math.sqrt(tf);
//  }
//
  private float function_bm25_tf(float tf, float k1, float b, float dl, float avgdl) {
//    float k1 = 1.2f;
//    float b = 0.75f;

    return tf / (tf + k1 * (1 - b + b * dl / avgdl));
  }

  private float sin(float x) {
    return (float) Math.sin(x);
  }


  private float function_line(float x, float m, float b) {
    return m * x + b;
  }

//  private float function_1(float x) {
//    //return (float) Math.sin(x);
//
////    float m = 1.0f;
////    float b = 0;
////    return m*x+b;
//
//  //    return (float) Math.sqrt(x);
//
//
//    float k1 = 1.2f;
//    float b = 0.75f;
//    float dl = 1.0f;
//    float avgdl = 10.0f;
//
//    return x / (x + k1 * (1 - b + b * dl / avgdl));
//  }

  @Override
  public void draw(int step) {
    draw_grid();

    for (float x = START_X; x <= MAX_X; x += interval) {
      presentation.strokeWeight(5);

//      float y = function_classic_idf((long) x, 1000);
//      Point p = new Point(x, y);
//      presentation.stroke(Color.decode("#FF0000").getRGB());
//      p.draw();
//
//      y = function_bm25_idf((long) x, 1000);
//      p = new Point(x, y);
//      presentation.stroke(Color.decode("#00FF00").getRGB());
//      p.draw();
//
//      y = function_line(x, 0.1f, 0);
//      p = new Point(x, y);
//      presentation.stroke(Color.decode("#0000FF").getRGB());
//      p.draw();
      int c1 = Color.getHSBColor(204, 102, 0).getRGB();
      int c2 = Color.getHSBColor(0, 102, 153).getRGB();

      for (int i = 0; i < functions.size(); i++) {
        Function<Float, Float> f = functions.get(i);
        int f_color = presentation.lerpColor(c1, c2, (float) (i + 1) / (float) functions.size());
        float y = f.apply(x);
        Point p = new Point(x, y);
        presentation.stroke(f_color);
        p.draw();
      }


//      y = sin(x);
//      p = new Point(x, y);
//      presentation.stroke(Color.decode("#AA00FF").getRGB());
//      p.draw();


//      y = function_classic_tf(x);
//      p = new Point(x,y);
//      presentation.stroke(Color.decode("#FF0000").getRGB());
//      p.draw();
//
//      y = function_bm25_tf(x, 1.2f, 0.75f, 50.0f, 75.0f);;
//      p = new Point(x,y);
//      presentation.stroke(Color.decode("#0000FF").getRGB());
//      p.draw();
    }

    super.draw(step);
  }

  private void draw_grid() {
    presentation.push();

    // Draw axis/grid
    presentation.strokeWeight(1);
    presentation.stroke(presentation.black);
    presentation.fill(presentation.black);
    line(left_middle, right_middle);  // x-axis
    line(bottom_middle, top_middle);  // y-axis

    // hash marks
    int num_ticks = 10;
    float x_interval = (MAX_X - MIN_X) / num_ticks;
    float y_interval = (MAX_Y - MIN_Y) / num_ticks;
    float x_tick_size = 0.1f;
    float y_tick_size = 0.1f;
    for (int i = 1; i <= num_ticks; i++) {
      float x = i * x_interval;
      line(new Point(x, -(x_tick_size / 2)), new Point(x, x_tick_size / 2));
      line(new Point(-x, -(x_tick_size / 2)), new Point(-x, x_tick_size / 2));
    }
    for (int i = 1; i <= num_ticks; i++) {
      float y = i * y_interval;
      line(new Point(-(y_tick_size / 2), y), new Point(y_tick_size / 2, y));
      line(new Point(-(y_tick_size / 2), -y), new Point(y_tick_size / 2, -y));
    }

    presentation.pop();
  }

  private void line(Point p1, Point p2) {
    presentation.line(p1.map_x(), p1.map_y(), p2.map_x(), p2.map_y());
  }

  private class Point {
    private final float x;
    private final float y;

    public Point(float x, float y) {
      this.x = x;
      this.y = y;
    }

    public void draw() {
      presentation.point(map_x(), map_y());
    }

    private float map_y() {
      return PApplet.map(y, MIN_Y, MAX_Y, presentation.height, 0);
    }

    private float map_x() {
      return PApplet.map(x, MIN_X, MAX_X, 0, presentation.width);
    }

    public void print() {
      print("");
    }

    private void print(String prefix) {
      presentation.println(("".equals(prefix) ? "" : (prefix + ":")), x, y, " => ", map_x(), map_y());
    }
  }

  private static class Line {
    private final float m;
    private final float b;

    public Line(float m, float b) {
      this.m = m;
      this.b = b;
    }

    public float f(float x) {
      return m * x + b;
    }

    public static Line factory(float m, float b) {
      return new Line(m, b);
    }
  }

  private static class BM25 {

    private final float k1;
    private final float b;
    private final float dl;
    private final float avgdl;

    public BM25(float k1, float b, float dl, float avgdl) {
      this.k1 = k1;
      this.b = b;
      this.dl = dl;
      this.avgdl = avgdl;
    }

    public float f(float tf) {
      return tf / (tf + k1 * (1 - b + b * dl / avgdl));
    }

    public static BM25 factory(float k1, float b, float dl, float avgdl) {
      return new BM25(k1, b, dl, avgdl);
    }
  }
}
