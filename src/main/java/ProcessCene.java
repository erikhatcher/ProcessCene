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

  private PImage footer_logo;

  private final String text = "Q-36 SpaceModulator"; //DEFAULT_TEXT;
  // TODO: fix "Text with an emoiji 😀 and Chinese 你好"; // this one has display issues (though window title renders accurately)

  @Override
  public void settings() {
    size(1500, 820);
  }

  @Override
  public void setup() {
    // TODO: Make this a keyboard toggle to Solr, AS, ES, Lucene...
    TextAnalyzer text_analyzer = new LuceneAnalyzer(); // new SolrAnalyzer();

    frameRate(10);
    fill(black);

    textFont(loadFont(getFilePathFromResources("LexendDeca-Light-24.vlw")));

    PImage toc_bullet_image = loadImage(getFilePathFromResources("Assets/normal/General_ACTION_Favorite_Inverted10x.png"));
    toc_bullet_image.resize(50, 0);

    PImage bullet_image = loadImage(getFilePathFromResources("Assets/normal/Technical_MDB_WildcardIndex10x.png"));
    bullet_image.resize(30, 0);

    footer_logo = loadImage(getFilePathFromResources("mongodb-assets/MongoDB_Logomark-SpringGreen/MongoDB_Logo.png"));
    footer_logo.resize(0, 30);

    PImage qr_code = loadImage(getFilePathFromResources("uberconf_qr_code.png"));

    PImage lucene_in_action_cover = loadImage(getFilePathFromResources("lucene_in_action.png"));
    lucene_in_action_cover.resize(0, 600);
    PImage apache_feather = loadImage(getFilePathFromResources("apache_feather.png"));
    apache_feather.resize(250,0);
    PImage blacklight_logo = loadImage(getFilePathFromResources("blacklight_logo.png"));
    PImage lucidworks_logo = loadImage(getFilePathFromResources("lucidworks_logo.png"));
    lucidworks_logo.resize(500,0);
    PImage mdb_logo = loadImage(getFilePathFromResources("mongodb-assets/MongoDB_Spring-Green/MongoDB_SpringGreen.png"));
    mdb_logo.resize(0,300);

    PImage its_just_search_results = loadImage(getFilePathFromResources("anatomy_of_search_results.png"));
    its_just_search_results.resize(0,600);
    PImage its_just_search_grouping = loadImage(getFilePathFromResources("grouping_find_a_doctor.png"));
    its_just_search_grouping.resize(0,600);

    PImage lucene_logo = loadImage(getFilePathFromResources("lucene_green_300.png"));
    lucene_logo.resize(300,0);

    PImage solr_logo = loadImage(getFilePathFromResources("Solr_Logo_on_white.png"));
    solr_logo.resize(0, 150);

    PImage atlas_search_logo = loadImage(getFilePathFromResources("mongodb-assets/SearchDocument/Technical_ACTION_SearchDocument_Spot_BS_ForestGreen.png"));
    atlas_search_logo.resize(300, 0);

    PImage atlas_big_picture = loadImage(getFilePathFromResources("atlas_big_picture.png"));
    atlas_big_picture.resize(0, 600);
    PImage atlas_search_ui_1 = loadImage(getFilePathFromResources("atlas_search_ui_1.png"));
    atlas_search_ui_1.resize(0, 600);
    PImage atlas_search_ui_2 = loadImage(getFilePathFromResources("atlas_search_ui_2.png"));
    atlas_search_ui_2.resize(0, 700);
    PImage atlas_search_ui_3 = loadImage(getFilePathFromResources("atlas_search_ui_3.png"));
    atlas_search_ui_3.resize(0, 650);
    PImage atlas_search_ui_4 = loadImage(getFilePathFromResources("atlas_search_ui_4.png"));
    atlas_search_ui_4.resize(0, 650);
    PImage atlas_search_ui_5 = loadImage(getFilePathFromResources("atlas_search_ui_5.png"));
    atlas_search_ui_5.resize(0, 650);
    PImage atlas_search_ui_6 = loadImage(getFilePathFromResources("atlas_search_ui_6.png"));
    atlas_search_ui_6.resize(0, 600);
    PImage atlas_search_ui_7 = loadImage(getFilePathFromResources("atlas_search_ui_7.png"));
    atlas_search_ui_7.resize(0, 600);
    PImage atlas_search_ui_8 = loadImage(getFilePathFromResources("atlas_search_ui_8.png"));
    atlas_search_ui_8.resize(0, 650);
    PImage atlas_search_ui_9 = loadImage(getFilePathFromResources("atlas_search_ui_9.png"));
    atlas_search_ui_9.resize(0, 600);
    PImage atlas_search_ui_10 = loadImage(getFilePathFromResources("atlas_search_ui_10.png"));
    atlas_search_ui_10.resize(0, 600);
    PImage atlas_search_ui_11 = loadImage(getFilePathFromResources("atlas_search_ui_11.png"));
    atlas_search_ui_11.resize(0, 650);

    slides.add(new UberconfTitleSlide(this));
      slides.add(new SplashSlide("About Me", white,
          new PImage[] {apache_feather, lucene_in_action_cover, blacklight_logo, lucidworks_logo, mdb_logo},
        "https://mdb.link/erik", this).setShowOnTOC(false));
      slides.add(new AboutProcessCene("About ProcessCene", this).setShowOnTOC(false));

    slides.add(new SplashSlide("\"It's Just Search\": features of Lucene", white,
        new PImage[] {its_just_search_results, its_just_search_grouping}, "", this));

    slides.add(new InvertedIndexSlide("Inverted Index", this));

    slides.add(new AnalysisSlide("Text Analysis", text_analyzer, text, this));
      slides.add(new AllyzersSlide("Analyzers", text_analyzer, text, this).setShowOnTOC(false));
      slides.add(new BulletPointsSlide("Language Capabilities",
          new String[] { "i18n / ICU / character folding", "word decompounding", "stemming", "phonetic"}, bullet_image, null, this).setShowOnTOC(false));

    slides.add(new BulletPointsSlide("Other Index Types",
        new String[] { "Numeric", "Spatial", "FST: Finite State Transducer", "Vector" }, bullet_image, null,this));

    slides.add(new BulletPointsSlide("Query building",
        new String[] { "Query API", "query parsing", "query intent (see Solr Tagger)" }, bullet_image, null, this));
      slides.add(new QueryParsingSlide("Query Parsing", this).setShowOnTOC(false));

    slides.add(new BulletPointsSlide("Searching",
        new String[] {"Filtering", "Relevancy Scoring"}, bullet_image, null, this));
      slides.add(new BulletPointsSlide("Filtering",
          new String[] { "non-scoring", "efficient skipping", "caching"}, bullet_image, null, this).setShowOnTOC(false));
      slides.add(new BulletPointsSlide("Relevancy Scoring",
          new String[] {"TF/IDF", "BM25", "Function boosting"}, bullet_image, null, this).setShowOnTOC(false));

    slides.add(new BulletPointsSlide("Core Lucene Ecosystem",
        new String[] { "Highlighting", "Suggest", "Spatial", "Facets", "Lucene Monitor", "MLT",
            "Expressions", "Grouping", "Block join, parent/child", "Luke", "Vector Search"}, bullet_image, lucene_logo, this));
      slides.add(new VectorSearchSlide("Vector Search", this).setShowOnTOC(false));

    slides.add(new BulletPointsSlide("Lucene Inside",
        new String[] {"Solr", "elasticsearch", "OpenSearch", "Lucidworks", "Atlas Search", "..."}, bullet_image, null,this));
      slides.add(new BulletPointsSlide("Solr",
          new String[] {"Tagger",
                        "streaming expressions",
                        "joins",
                        "robust query parsing flexibility",
                        "authentication / authorization",
                        "extensible",
                        "plugins: rich documents, etc"},
          bullet_image, solr_logo, this).setShowOnTOC(false));
      slides.add(new SolrTaggerSlide("Solr Tagger", this).setShowOnTOC(false));
      slides.add(new BulletPointsSlide("Atlas Search", mist,
          new String[] {"Click, click, click, it's real easy!",
                        "Hosted",
                        "Replicated",
                        "Keeps database and search in sync",
                        "Supports nested/embedded documents",
                        "Flexible field type handling",
                        "https://www.mongodb.com/atlas/search"}, bullet_image, atlas_search_logo ,this).setShowOnTOC(false));
      slides.add(new SplashSlide("Atlas Search", mist,
          new PImage[] {atlas_big_picture, atlas_search_ui_1, atlas_search_ui_2, atlas_search_ui_3, atlas_search_ui_4, atlas_search_ui_5, atlas_search_ui_6, atlas_search_ui_7, atlas_search_ui_8, atlas_search_ui_9, atlas_search_ui_10, atlas_search_ui_11},
          "Atlas Search", this).setShowOnTOC(false));
      slides.add(new AtlasSearchQueryingSlide("Atlas Search: Querying", this).setShowOnTOC(false));

    slides.add(new SplashSlide("Go forth and search...", white, qr_code, "https://mdb.link/uberconf", this));

    // * Demonstrate solutions to https://www.mongodb.com/blog/post/three-ways-retailers-use-search-beyond-ecommerce-store

    TableOfContentsSlide toc_slide = new TableOfContentsSlide(slides, toc_bullet_image, this);
    slides.add(1, toc_slide);

    boolean inject_toc_guide = true;
    if (inject_toc_guide) {
      List<Slide> updated_slides = new ArrayList<>();
      for (int i = 0; i < slides.size(); i++) {
        Slide this_content_slide = slides.get(i);

        // Inject tracking ToC slide before every slide that appears on the ToC
        if (this_content_slide.getShowOnTOC() && !(this_content_slide instanceof TableOfContentsSlide)) {
          updated_slides.add(new TableOfContentsSlide(slides,toc_bullet_image,i,this));
        }

        updated_slides.add(this_content_slide);
      }

      slides = updated_slides;

    }
  }

  @Override
  public void draw() {
    background(white);

    Slide current_slide = slides.get(current_slide_index);
    int step = current_slide.getCurrentStep();
    int number_of_steps = current_slide.getNumberOfSteps();
    String title = (current_slide.getTitle() != null) ? current_slide.getTitle() : "";

    push();
    current_slide.draw(step);
    pop();

    // Draw the Footer
    if ((current_slide_index > 0) && show_footer) {
      String slide_counter = current_slide_index + 1 + "/" + slides.size();
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
        image(footer_logo, 10, height - footer_logo.height - 2);
      }
    }
  }

  @Override
  public void keyTyped(KeyEvent event) {
    Slide current_slide = slides.get(current_slide_index);

    char key = event.getKey();

//    int modifiers = event.getModifiers();
//    int keyCode = event.getKeyCode();
//    boolean ctrl = event.isControlDown();
//    boolean alt = event.isAltDown();
//    boolean meta = event.isMetaDown();
//
//    System.out.print("keyTyped: " + modifiers + " " + keyCode + " ");
//    System.out.print((ctrl) ? "Ctrl-" : "");
//    System.out.print((alt) ? "Alt-" : "");
//    System.out.print((meta) ? "Meta-" : "");
//    System.out.println(key);

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
          setSlide(current_slide_index - 1);
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

      case '0':
        setSlide(0);
        break;

      case 't':
        setSlide(1);
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