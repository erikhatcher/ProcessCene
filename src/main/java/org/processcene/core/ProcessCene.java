package org.processcene.core;

import org.apache.lucene.document.Document;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessCene extends PApplet {
  private final List<Document> documents = new ArrayList<>();
  protected List<Slide> slides = new ArrayList<>();

  private boolean show_footer = true;
  private int current_slide_index = 0;
  public Theme theme;

  // TODO: Colors need to be migrated to Theme
  // Primary Colors
  public int spring_green = Color.decode("#00ED64").getRGB();
  public int forest_green = Color.decode("#00684A").getRGB();
  public int evergreen = Color.decode("#023430").getRGB();

  // Secondary Colors
  public int mist = Color.decode("#E3FCF7").getRGB();
  public int lavender = Color.decode("#F9EBFF").getRGB();


//  public ProcessCene() {
//
//    // queries = new ArrayList<>();
//
//    // has a PApplet?  or gets passed one to draw into?
//
//    // has a Theme? (that specifies colors and ways to get images/shapes)
//  }

  protected static void run(String name) {
    PApplet.main(new String[]{name});
  }

  /**
   * Adds a slide to the end of the presentation
   *
   * @param s Slide
   * @return this, builder-chain style
   */
  public ProcessCene add(Slide s) {
    slides.add(s);
    return this;
  }

  public void setSlide(int i) {
    current_slide_index = i;
    Slide current_slide = slides.get(current_slide_index);
    current_slide.setVariation(1);
    current_slide.setStep(0);
  }

  protected void setTheme(Theme theme) {
    this.theme = theme;
  }


  public static String getFilePathFromResources(String resource) {
    return ProcessCene.class.getResource("/" + resource).getPath();
  }

  @Override
  public void settings() {
    size(1500, 820);
  }

  @Override
  public void setup() {
    frameRate(10);


    theme.init(this);
    slides.forEach(s -> {
      s.init(this);
    });

    fill(theme.foreground);

//    org.processcene.core.TableOfContentsSlide toc_slide = new org.processcene.core.TableOfContentsSlide(slides, toc_bullet_image, this);
//    slides.add(1, toc_slide);
//
//    boolean inject_toc_guide = true;
//    if (inject_toc_guide) {
//      List<org.processcene.core.Slide> updated_slides = new ArrayList<>();
//      for (int i = 0; i < slides.size(); i++) {
//        org.processcene.core.Slide this_content_slide = slides.get(i);
//
//        // Inject tracking ToC slide before every slide that appears on the ToC
//        if (this_content_slide.getShowOnTOC() && !(this_content_slide instanceof org.processcene.core.TableOfContentsSlide)) {
//          updated_slides.add(new org.processcene.core.TableOfContentsSlide(slides,toc_bullet_image,i,this));
//        }
//
//        updated_slides.add(this_content_slide);
//      }
//
//      slides = updated_slides;
//    }
  }

  @Override
  public void draw() {
    background(theme.background);

    Slide current_slide = slides.get(current_slide_index);
    int step = current_slide.getCurrentStep();
    int number_of_steps = current_slide.getNumberOfSteps();
    String title = (current_slide.getTitle() != null) ? current_slide.getTitle() : "";

    push();
    current_slide.draw(this, step);
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

      if (theme.footer_logo != null) {
        image(theme.footer_logo, 10, height - theme.footer_logo.height - 2);
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
  }

  @Override
  public void mouseClicked(MouseEvent event) {
    Slide current_slide = slides.get(current_slide_index);
    current_slide.mouseClicked(event);
  }
}
