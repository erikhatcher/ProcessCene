import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PShape;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Processing with Lucene
 */
public class ProcessCene extends PApplet {
  private boolean animate_intra_slide = false;

  // TODO: Extract these colors to a "theme" abstraction with MongoDB-specific one
  // Primary Colors
  public Color slate = Color.decode("#001E2B");
  public Color white = Color.decode("#FFFFFF");
  public Color spring_green = Color.decode("#00ED64");
  public Color forest_green = Color.decode("#00684A");
  public Color evergreen = Color.decode("#023430");

  // Secondary Colors
  public Color mist = Color.decode("#E3FCF7");
  public Color lavender = Color.decode("#F9EBFF");


  public static void main(String[] args) {
    PApplet.main(new String[]{ProcessCene.class.getName()});
  }

  List<Slide> slides = new ArrayList<Slide>();
  int current_slide_index = 0;
  public int step = 0;  // TODO: revisit: shouldn't be public, but slides need to control it
  private boolean animate = false;

  private PImage footer_logo;

  private final String DEFAULT_TEXT = "The quick brown fox jumped over the lazy dogs.";
  private final String text = "Q-36 SpaceModulator"; //DEFAULT_TEXT;
  // "Text with an emoiji 😀 and Chinese 你好"; // this one has display issues (though window title renders accurately)

  @Override
  public void settings() {
    size(1024, 768);
  }

  @Override
  public void setup() {
    frameRate(5);
    fill(0);

    textFont(loadFont(getFilePathFromResources("CourierNewPSMT-24.vlw")));

    PImage lucene_logo = loadImage(getFilePathFromResources("lucene_green_300.png"));
//    PImage bullet_image = loadImage(getFilePathFromResources("mongodb-assets/search_check.png"));
    PImage bullet_image = loadImage(getFilePathFromResources("Assets/normal/General_ACTION_Favorite_Inverted10x.png"));
    bullet_image.resize(50, 0);
    PShape processing_logo = loadShape(getFilePathFromResources("Processing_2021_logo.svg"));
    footer_logo = loadImage(getFilePathFromResources("uberconf_brain.jpeg"));
    footer_logo.resize(50, 0);

    PImage qr_code = loadImage(getFilePathFromResources("uberconf_qr_code.png"));


    // TODO: Make this a keyboard toggle to Solr, AS, ES, Lucene...
    TextAnalyzer text_analyzer = new LuceneAnalyzer(); // new SolrAnalyzer();

    slides.add(new UberconfTitleSlide("Love of Lucene",this));
    // slides.add(new SplashSlide("Love of Lucene", 255, lucene_logo, false,this));
    slides.add(new LuceneIndexingSlide("Lucene Indexing", this));
    slides.add(new AnalysisSlide(text_analyzer, text, this));
    slides.add(new AllyzersSlide(text_analyzer, text, this));
    // slides.add(new SplashSlide(null, 0, processing_logo, this));
    // slides.add(new AtlasSearchQueryingSlide("Atlas Search: Querying", this));
    slides.add(new SplashSlide("QR Code", 255, qr_code, false, this));

    // TODO: Add slides for the following topics:
    //   - Lucene lower-level - segments
    //   - Geo search (map, circle radius, pins for documents)
    //   - Suggest
    //   - FST / Tagger (use Solr Tagger)
    //   - Facets (Lucene and Solr both?)
    //   - Highlighting (show text passage and graphically highlighting)
    //   - Vector HNSW/KNN
    // * Demonstrate solutions to https://www.mongodb.com/blog/post/three-ways-retailers-use-search-beyond-ecommerce-store
    // * Resources:
    //   - https://www.atlassearchworkshop.com/
    //   - lucene.apache.org....

    TableOfContentsSlide toc_slide = new TableOfContentsSlide(slides, bullet_image, this);
    slides.add(1, toc_slide);
  }

  @Override
  public void draw() {
    Slide current_slide = slides.get(current_slide_index);
    int number_of_steps = current_slide.getNumberOfSteps();
    String title = (current_slide.getTitle() != null) ? current_slide.getTitle() : "";

    if (animate) {
      if (animate_intra_slide) {
        step = frameCount % (number_of_steps + 1);
      } else {
        step++;
      }
      if (step > number_of_steps) {
        // advance the slide
        if (current_slide_index < slides.size() - 1) {
          current_slide_index++;
          current_slide = slides.get(current_slide_index);
          number_of_steps = current_slide.getNumberOfSteps();
          step = 0;
        }

        if (current_slide_index == (slides.size() - 1) && step > number_of_steps) {
          // stay on last step of last slide when we hit it
          step = number_of_steps;
        }
      }
    }

//    println("Slide: " + current_slide_index + " " + step + "/" + number_of_steps);
    push();
    current_slide.draw(step);
    pop();

    // Draw the Footer
    String slide_counter = (current_slide_index + 1) + "/" + slides.size() +
        ((number_of_steps > 0) ? " [Step: " + step + "/" + number_of_steps + "]" : "");
    text(slide_counter, width - textWidth(slide_counter) - 10, height - textDescent());
    String slide_title = title; //this.getClass().getName() + title;
    text(slide_title, (width - textWidth(slide_title)) / 2, height - textDescent());
    if (footer_logo != null) {
      image(footer_logo, 0, height - footer_logo.height);
    }

//    PShape lucene_logo = loadShape(getFilePathFromResources("lucene_logo_retro.svg"));
//    if (footer_logo != null) {
//      shape(lucene_logo, 0, height - lucene_logo.height);
//    }

  }

  @Override
  public void keyTyped(KeyEvent event) {
    char key = event.getKey();
    int modifiers = event.getModifiers();
    int keyCode = event.getKeyCode();
    boolean ctrl = event.isControlDown();
    boolean alt = event.isAltDown();
    boolean meta = event.isMetaDown();

    System.out.print("keyTyped: " + modifiers + " " + keyCode + " ");
    System.out.print((ctrl) ? "Ctrl-" : "");
    System.out.print((alt) ? "Alt-" : "");
    System.out.print((meta) ? "Meta-" : "");
    System.out.println(key);

    if (key == ' ') {
      if (current_slide_index < slides.size() - 1) {
        current_slide_index++;
        step = 0;
      }
    }

    if (ctrl && !alt) {
      // ctrl+`key`

      switch (key) {
        case '=':
          step = 0;
          current_slide_index = 0;
          animate = false;
          break;

        case '`':
          animate = !animate;
          break;

        case 'A':  // TODO: fix this one
          animate_intra_slide = !animate_intra_slide;
          println(animate_intra_slide);
          break;

        case ',': // previous step
          if (step > 0) {
            step--;
          }
          break;

        case '.': // next step
          Slide current_slide = slides.get(current_slide_index);
          int number_of_steps = current_slide.getNumberOfSteps();
          if (step < number_of_steps) {
            step++;
          }
          break;

// TODO: how to catch ctrl-x?
//        case 'x':
//        case 'X':
//          exit();
//          break;

        default:
          if (key >= '1' && key <= '9') {
            if ((key - '1') < slides.size())
              current_slide_index = key - '1';
            step = 0;
          }
          break;
      }
    } else {
      // pass down to current slide to handle or not
      Slide current_slide = slides.get(current_slide_index);
      current_slide.keyTyped(event);
    }
  }

  @Override
  public void mouseClicked(MouseEvent event) {
    Slide current_slide = slides.get(current_slide_index);
    current_slide.mouseClicked(event);
  }

  public void setSlide(int i) {
    current_slide_index = i;
  }

  public String getFilePathFromResources(String resource) {
    return getClass().getResource(resource).getPath();
  }
}

//  private class MongoDiagram {
//
//    String imagePath = "Assets";
//    int diagramWidth = 800;
//    int diagramHeight = 600;
//    //Next part is only used if you want o add some space on the left - needs more thought.
//    int fixedWidth = 800; //If diagram size is larger than fixed sizes then we support >100% placing
//    int fixedHeight = 600; //Used if we change the 'Canvas' size and dont want to resize
//    float xScale = fixedWidth / 100;
//    float yScale = fixedHeight / 100;
//    int connectorEndSize=10;
//
//    //Refer to MongoDB Brand Book v2.0 for Spec
//
//    color springGreen = #00ED64;
//    color forestGreen  =  #00684A;
//    color everGreen  =  #023430;
//    color slateBlue = #001E2B;
//    color mist = #E3FCF7;
//    color lavender = #F9EBFF;
//    color black = color(0);
//    color white = color(255);
//
//    //TODO scale to match image size
//    PFont labelFont = null;
//    int labelFontSize = 16;
//
//
//    MongoDiagram(int dwidth, int dheight, String path)
//    {
//      background(white);
//      labelFont = createFont("LexendDeca-Light", labelFontSize, true);
//      // I prefer to speficy center, width and height rather than top corner
//      rectMode(CENTER);
//      imageMode(CENTER);
//      textAlign(CENTER, CENTER);
//
//      this.diagramWidth=dwidth;
//      this.diagramHeight=dheight;
//      this.imagePath=path;
//    }
//
//
//
//
//    //Setup called on program start
//
//
//
//    Rect drawConnector(float x1, float y1, float x2, float y2) {
//      // A line from X1,Y1 to X2/Y2 with a black ball on the end
//      stroke(black);
//      fill(black);
//      line((int)x1*xScale, (int)y1*yScale, (int)x2*xScale, (int)y2*yScale);
//      circle((int)x2*xScale, (int)y2*yScale, connectorEndSize);
//      //Return a bounting box for the line
//      return new Rect((x1+x2)/2, (y1+y2)/2, abs(x1-x2), abs(y2-y1));
//    }
//
//    void drawLabel(Rect r, String labelText) {
//      drawLabel(r.x, r.y, r.w, r.h, labelText);
//    }
//    void drawLabel(float x, float y, float w, float h, String labelText)
//    {
//      textFont(labelFont);
//      fill(black);
//      text(labelText, x * xScale, y * yScale, w*xScale, h*yScale);
//    }
//
//    Rect inside(Rect rect) {
//      return rect;
//    }
//
//    //Need to define height of new box sorta
//    Rect under(Rect rect, float distunder) {
//      return new Rect(rect.x, rect.y+(rect.h/2)+distunder, rect.w, distunder*2);
//    }
//
//
//
//    Rect drawContainer(float x, float y, float w, float h, color fillColour, boolean baseLine)
//    {
//      int barHeight = 20;
//      fill(fillColour);
//      stroke(black);
//      rect((int)(x * xScale), (int)(y * yScale), (int)(w*xScale), (int)(h*yScale));
//      if (baseLine) {
//        fill(black);
//        rect((int)(x * xScale), (int)(y+(h/2)) * yScale-(barHeight/2), (int)(w*xScale), barHeight);
//      }
//      return new Rect(x, y, w, h);
//    }
//
//    //Defaulting 1x size to being a percentage of the window width
//    //Also X and Y are percentages not absolute pixels
//
//    Rect drawIcon(String name, float x, float y, float relativeSize)
//    {
//      PImage icon;
//      icon = loadImage(imagePath + "/normal/"+name+".png");
//      int defaultScale = 10; //10%
//      //scale to 10% of image size * relativesize
//      float scalefactor =  (fixedWidth*defaultScale*relativeSize)/(icon.height*100);
//      //Explicit PImage Resize is much nicer than the resize when rendering with image()
//      icon.resize((int)(icon.width*scalefactor), (int)(icon.height*scalefactor));
//      image(icon, (int)x*xScale, (int)y*yScale);
//      return new Rect(x, y, relativeSize*defaultScale, relativeSize*defaultScale*(icon.height/icon.width));
//    }
//
//    //TODO - Create a 'Relative' coordinate options so you can do under(user)
//    void debugLayout(Rect r) {
//      stroke(color(255, 0, 0));
//      noFill();
//      rect(r.x*xScale, r.y*yScale, r.w*xScale, r.h*yScale);
//    }
//  }


/*

case '\\':
    current_slide_index++;
    if (current_slide_index >= slides.size()) {
        current_slide_index = slides.size() - 1;
    }
    break;


  switch(key) {

    case ']': // next analyzer
      analyzer_index++;
      if (analyzer_index >= analyzers.size()) {
        analyzer_index = 0;
      }
      reset();
      break;

    case '-': // clear text
      if (!offline_mode) {
        text = "";
      }
      reset();
      break;

    case '\\': // advance the slide
      current_slide_index++;
      if (current_slide_index >= slides.size()) {
        current_slide_index = slides.size() - 1;
      }
      step = 0;
      break;

    default:
      if (!offline_mode) {  // not yet implemented: need to run analysis here for each character typed
        if ((key >= 'A' && key <= 'z') || key == ' ') {
          text += key;
        }
        reset();
      }

      if (key >= '0' && key <= '9') {
        current_slide_index = key - '0';
        step = 0;
      }
      break;
    }

    println("Key: ", key, "Slide: ",current_slide_index,"Step: ",step);
    windowTitle("Slide: " + current_slide_index + " Step: " + step);

 */
