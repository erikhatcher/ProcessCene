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
  private boolean show_footer = true;

  public static void main(String[] args) {
    PApplet.main(new String[]{ProcessCene.class.getName()});
  }

  private List<Slide> slides = new ArrayList<Slide>();
  private int current_slide_index = 0;
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


    slides.add(new UberconfTitleSlide(this));
    slides.add(new SplashSlide("About Me", this.spring_green, null,
        "mdb.link/erik", this).setShowOnTOC(false));

    slides.add(new SplashSlide("\"It's Just Search\": features of Lucene", spring_green, null,
        "fill in with a image of it's just search presentation slide?", this));

    slides.add(new InvertedIndexSlide("Inverted Index", this));

    slides.add(new AnalysisSlide(text_analyzer, text, this));
      slides.add(new AllyzersSlide(text_analyzer, text, this).setShowOnTOC(false));
      slides.add(new SplashSlide("Language Considerations", spring_green, null,
          "i18n, character folding, ICU, word decompounding, phonetic", this).setShowOnTOC(false));

    slides.add(new SplashSlide("Index Types", spring_green, null,
        "TBD\n\nNumerics, Spatial, Vectors, FST", this));

    slides.add(new SplashSlide("Querying", spring_green, null,
        "TBD\n\nQuery API, query generation, query parsing, .... query intent?", this));
      slides.add(new QueryParsingSlide("Query Parsing", this).setShowOnTOC(false));

    slides.add(new SplashSlide("Searching", spring_green, null,
        "TBD", this));
      slides.add(new SplashSlide("Filtering", spring_green, null,
          "TBD\n\nskipping / eventual DocSet view animating skipping", this).setShowOnTOC(false));
      slides.add(new SplashSlide("Relevancy Scoring", spring_green, null,
          "TBD\n\nsimilarity / TF/IDF / BM25", this).setShowOnTOC(false));

    slides.add(new SplashSlide("Lucene Inside", spring_green, null,
        "TBD\n\nSolr, elasticsearch, OpenSearch, Atlas Search\nLucidworks, ...", this));
      slides.add(new SplashSlide("Solr", spring_green, null,
          "TBD\n\nSolr logo, highlights: e.g. tagger!", this).setShowOnTOC(false));
      slides.add(new SolrTaggerSlide("Solr Tagger", this).setShowOnTOC(false));
      slides.add(new AtlasSearchQueryingSlide("Atlas Search: Querying", this).setShowOnTOC(false));

    slides.add(new SplashSlide("More Like This: The Lucene Ecosystem", spring_green, null,
        "TBD\n\nHighlighting, Suggest, Spatial, Facets, Lucene Monitor, MLT\n" +
               "expressions, grouping, join, parent/child, Luke", this));
      slides.add(new SplashSlide("Vectors", spring_green, null,
          "TBD\n\nVector Search with Lucene API, HNSW/KNN", this).setShowOnTOC(false));

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
    int step = current_slide.getCurrentStep();
    int number_of_steps = current_slide.getNumberOfSteps();
    String title = (current_slide.getTitle() != null) ? current_slide.getTitle() : "";

//    if (animate) {
//      if (animate_intra_slide) {
//        step = frameCount % (number_of_steps + 1);
//      } else {
//        step++;
//      }
//      if (step > number_of_steps) {
//        // advance the slide
//        if (current_slide_index < slides.size() - 1) {
//          current_slide_index++;
//          current_slide = slides.get(current_slide_index);
//          number_of_steps = current_slide.getNumberOfSteps();
//          step = 0;
//        }
//
//        if (current_slide_index == (slides.size() - 1) && step > number_of_steps) {
//          // stay on last step of last slide when we hit it
//          step = number_of_steps;
//        }
//      }
//    }

    push();
    current_slide.draw(step);
    pop();

    // Draw the Footer
    if (show_footer) {
      String slide_counter = current_slide_index + "/" + slides.size();
      text(slide_counter, width - textWidth(slide_counter) - 10, height - textDescent());
      String slide_title = title;
      text(slide_title, (width - textWidth(slide_title)) / 2, height - textDescent());

      if (number_of_steps > 0) {
        String step_text = "Step: " + step + "/" + number_of_steps;
        text(step_text, width - textWidth(step_text) - 10, height - 2 * (textAscent()));
      }

      if (current_slide.getNumberOfVariations() > 0) {
        String variation_text = "Variation: " + current_slide.getCurrentVariation() + "/" + current_slide.getNumberOfVariations();
        text(variation_text, width - textWidth(variation_text) - 10, height - 3 * (textAscent()));
      }

      if (footer_logo != null) {
        image(footer_logo, 0, height - footer_logo.height);
      }
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

    switch (key) {
      // Move forward one step at a time, advance slide when at end
      case ' ':
        if (current_slide.getCurrentStep() < current_slide.getNumberOfSteps()) {
          current_slide.setStep(current_slide.getCurrentStep() + 1);
        } else {
          if (current_slide.getCurrentVariation() < current_slide.getNumberOfVariations()) {
            current_slide.setVariation(current_slide.getCurrentVariation() + 1);
          } else {
            if (current_slide_index < slides.size() - 1) {
              setSlide(current_slide_index + 1);
            }
          }
        }
        break;

      // Advance to next slide, skipping any remaining steps of current slide
      case '\\':
        if (current_slide_index < slides.size() - 1) {
          setSlide(current_slide_index + 1);
        }
        break;

      // move to initial step of previous slide
      case ']':
        if (current_slide_index > 0) {
          current_slide_index--;
          current_slide.setStep(0);
        }
        break;

      // next step
      case '[':
        if (current_slide.getCurrentStep() < current_slide.getNumberOfSteps()) {
          current_slide.setStep(current_slide.getCurrentStep() + 1);
        }
        break;

      // previous step
      case '\'':
        if (current_slide.getCurrentStep() > 0) {
          current_slide.setStep(current_slide.getCurrentStep() - 1);
        }
        break;

      // next variation
      case '=':
        if (current_slide.getCurrentVariation() < current_slide.getNumberOfVariations()) {
          current_slide.setVariation(current_slide.getCurrentVariation() + 1);
        }
        break;

      // previous variation
      case '-':
        if (current_slide.getCurrentVariation() > 1) {
          current_slide.setVariation(current_slide.getCurrentVariation() - 1);
        }
        break;

      case 'f':
        show_footer = !show_footer;
        break;

      default:
        // Move to step 0 of Nth slide that isn't hidden
        if (key >= '1' && key <= '9') {
          int n = key - '1' + 1;
          if (n < slides.size()) {
            int main_slides_encountered = 0;
            for (int i = 0; i < slides.size(); i++) {
              Slide slide = slides.get(i);
              if (slide.getShowOnTOC()) {
                main_slides_encountered++;
                if (n == main_slides_encountered) {
                  current_slide_index = i;
                  current_slide.setStep(0);
                }
              }
            }
          }
        } else {
          current_slide.keyTyped(event);
        }
        break;
    }

//    if (ctrl && !alt) {
//      // ctrl+`key`
//
//      switch (key) {
//
//        case '`':
//          animate = !animate;
//          break;
//
//        case 'A':  // TODO: fix this one
//          animate_intra_slide = !animate_intra_slide;
//          println(animate_intra_slide);
//          break;
//      }
//    } else {
//      // pass down to current slide to handle or not
//    }
  }

  @Override
  public void mouseClicked(MouseEvent event) {
    Slide current_slide = slides.get(current_slide_index);
    current_slide.mouseClicked(event);
  }

  public void setSlide(int i) {
    current_slide_index = i;
    Slide current_slide = slides.get(current_slide_index);
    current_slide.setVariation(1);
    current_slide.setStep(0);
  }

  public String getFilePathFromResources(String resource) {
    return getClass().getResource(resource).getPath();
  }
}