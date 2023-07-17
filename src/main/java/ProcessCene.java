import processing.core.PApplet;
import processing.core.PImage;
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
  public int slate = Color.decode("#001E2B").getRGB();
  public int white = Color.decode("#FFFFFF").getRGB();
  public int spring_green = Color.decode("#00ED64").getRGB();
  public int forest_green = Color.decode("#00684A").getRGB();
  public int evergreen = Color.decode("#023430").getRGB();

  // Secondary Colors
  public int mist = Color.decode("#E3FCF7").getRGB();
  public int lavender = Color.decode("#F9EBFF").getRGB();

  // Other colors
  public int black = Color.decode("#000000").getRGB();


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
  // TODO: fix "Text with an emoiji 😀 and Chinese 你好"; // this one has display issues (though window title renders accurately)

  @Override
  public void settings() {
    size(1500, 820);
  }

  @Override
  public void setup() {
    frameRate(10);
    fill(black);

    textFont(loadFont(getFilePathFromResources("LexendDeca-Light-24.vlw")));

    PImage bullet_image = loadImage(getFilePathFromResources("Assets/normal/General_ACTION_Favorite_Inverted10x.png"));
    bullet_image.resize(50, 0);
    footer_logo = loadImage(getFilePathFromResources("uberconf_brain.jpeg"));
    footer_logo.resize(50, 0);

    PImage qr_code = loadImage(getFilePathFromResources("uberconf_qr_code.png"));

    // TODO: Make this a keyboard toggle to Solr, AS, ES, Lucene...
    TextAnalyzer text_analyzer = new LuceneAnalyzer(); // new SolrAnalyzer();


    // Outline:
    //   * Inverted Index
    //   * Analysis
    //   * Querying
    //   * Relevancy
    //   * Highlighting
    //   * Suggest
    //   * Spatial (map, circle radius, pins for documents)
    //   * Faceting
    //   * Lucene Monitor
    //   * i18n/ICU/language
    //   * Vectors (HNSW / KNN)
    //   * Advanced:
    //       - Lucene index segments
    //       - expressions
    //       - grouping
    //       - join
    //       - Luke
    //   * Engines using Lucene:
    //        - Open source: Solr, elasticsearch, OpenSearch
    //        - Commercial: Lucidworks, Atlas Search
    //   * Solr Tagger
    //      - FST mention
    //   * Atlas Search
    // TODO: see https://lucene.apache.org/core/9_7_0/ for other features to cover
    slides.add(new UberconfTitleSlide("",this));
    slides.add(new SplashSlide("About Me", this.spring_green, null,"TBD", this).setShowOnTOC(false));

    slides.add(new SplashSlide("\"It's Just Search\": features of Lucene", this.spring_green, null,"TBD", this));

    slides.add(new InvertedIndexSlide("Inverted Index", this));

    slides.add(new AnalysisSlide(text_analyzer, text, this));
      slides.add(new AllyzersSlide(text_analyzer, text, this).setShowOnTOC(false));
      slides.add(new SplashSlide("Language Considerations", this.spring_green, null,"TBD", this).setShowOnTOC(false));

    slides.add(new QueryParsingSlide("Query Parsing", this));

    slides.add(new SplashSlide("Searching", this.spring_green, null,"TBD", this));
      slides.add(new SplashSlide("Filtering", this.spring_green, null,"TBD\n\nskipping / eventual DocSet view animating skipping", this).setShowOnTOC(false));
      slides.add(new SplashSlide("Scoring", this.spring_green, null,"TBD\n\nsimilarity / TF/IDF / BM25", this).setShowOnTOC(false));

    slides.add(new SolrTaggerSlide("Solr Tagger", this));

    // TODO: handle offline mode or Atlas Search not being accessible, lazy load results so app is faster
    //slides.add(new AtlasSearchQueryingSlide("Atlas Search: Querying", this));

    slides.add(new SplashSlide("Go forth and search...", 255, qr_code, "https://mdb.link/uberconf", this));

    // * Demonstrate solutions to https://www.mongodb.com/blog/post/three-ways-retailers-use-search-beyond-ecommerce-store

    TableOfContentsSlide toc_slide = new TableOfContentsSlide(slides, bullet_image, this);
    slides.add(1, toc_slide);

    boolean inject_toc_guide = true;
    if (inject_toc_guide) {
      List<Slide> updated_slides = new ArrayList<>();
      for (int i = 0; i < slides.size(); i++) {
        Slide this_content_slide = slides.get(i);

        // Inject tracking ToC slide before every slide that appears on the ToC
        if (this_content_slide.getShowOnTOC() && !(this_content_slide instanceof TableOfContentsSlide)) {
          updated_slides.add(new TableOfContentsSlide(slides,bullet_image,i,this));
        }

        updated_slides.add(this_content_slide);
      }

      slides = updated_slides;
    }
  }

  @Override
  public void draw() {
    background(white);
    // TODO: how to make some slides animate fast, and others slower?
//    if (current_slide_index > 1) {
//      frameRate(0.3f);
//    }
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
    String slide_counter = (current_slide_index + 1) + "/" + slides.size();
    text(slide_counter, width - textWidth(slide_counter) - 10, height - textDescent());
    String slide_title = title;
    text(slide_title, (width - textWidth(slide_title)) / 2, height - textDescent());

    if ((number_of_steps > 0)) {
      String step_text = "Step: " + step + "/" + number_of_steps;
      text(step_text, width - textWidth(step_text) - 10, height - 2 * (textAscent()));
    }

    if (footer_logo != null) {
      image(footer_logo, 0, height - footer_logo.height);
    }
  }

  @Override
  public void keyTyped(KeyEvent event) {
    Slide current_slide = slides.get(current_slide_index);

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

    // forward to next slide: space bar
    if (key == ' ') {
      if (step < current_slide.getNumberOfSteps()) {
        step++;
      } else {
        if (current_slide_index < slides.size() - 1) {
          current_slide_index++;
          step = 0;
        }
      }
    }

    if (key == '\\') {
      if (current_slide_index < slides.size() - 1) {
        current_slide_index++;
        step = 0;
      }
    }

    if (key == ']') {
      if (current_slide_index > 0) {
        current_slide_index--;
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