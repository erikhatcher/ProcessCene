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

    textFont(loadFont(getFilePathFromResources("LexendDeca-Light-24.vlw")));

    PImage bullet_image = loadImage(getFilePathFromResources("Assets/normal/General_ACTION_Favorite_Inverted10x.png"));
    bullet_image.resize(50, 0);
    footer_logo = loadImage(getFilePathFromResources("uberconf_brain.jpeg"));
    footer_logo.resize(50, 0);

    PImage qr_code = loadImage(getFilePathFromResources("uberconf_qr_code.png"));


    // TODO: Make this a keyboard toggle to Solr, AS, ES, Lucene...
    TextAnalyzer text_analyzer = new LuceneAnalyzer(); // new SolrAnalyzer();

    slides.add(new UberconfTitleSlide("Love of Lucene",this));
    slides.add(new LuceneIndexingSlide("Lucene Indexing", this));
    slides.add(new AnalysisSlide(text_analyzer, text, this));
    slides.add(new AllyzersSlide(text_analyzer, text, this));
    slides.add(new AtlasSearchQueryingSlide("Atlas Search: Querying", this));
    slides.add(new SplashSlide("Resources", 255, qr_code, "https://mdb.link/uberconf", false, this));

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

    push();
    current_slide.draw(step);
    pop();

    // Draw the Footer
    String slide_counter = (current_slide_index + 1) + "/" + slides.size() +
        ((number_of_steps > 0) ? " [Step: " + step + "/" + number_of_steps + "]" : "");
    text(slide_counter, width - textWidth(slide_counter) - 10, height - textDescent());
    String slide_title = title;
    text(slide_title, (width - textWidth(slide_title)) / 2, height - textDescent());
    if (footer_logo != null) {
      image(footer_logo, 0, height - footer_logo.height);
    }
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