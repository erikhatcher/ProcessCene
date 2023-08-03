package org.processcene.core;

import processing.core.PApplet;

import java.util.List;

public class DocumentsSlide extends BaseSlide {
  private final List<DocumentAvatar> documents;

  private final SearchEngineAdapter engine;

  public DocumentsSlide(String title, SearchEngineAdapter engine) {
    super(title);

    this.engine = engine;
    documents = engine.getDocuments();
  }

  @Override
  public void init(ProcessCene p) {
    super.init(p);
  }

  @Override
  public int getNumberOfSteps() {
    return 1 + engine.getQueries().size(); //documents.size();
  }

  @Override
  public void draw(ProcessCene p, int step) {
    switch (step) {
      case 0:
        reset_em();
        circle_em(p);
        break;

      case 1:
        line_em_up_to(p, documents.size(), 0.0f);
        break;

      default:
        int query_index = step - 2;
        reset_em();

        SearchRequest request = engine.getQueries().get(query_index);
        SearchResponse response = engine.search(request);

        p.text(request.toString(), 30,30);
        if (response.error != null) {
          p.text(response.error, 30, 60);
        }
        for (ResponseDocument result : response.documents) {
          int id = result.id;
          float score = result.score;

          // TODO: inefficient, but loop over all documents matching on this `_id`
          for (DocumentAvatar document : documents) {
            if (id == document.id) {
              document.on = true;
              document.score = score;
            }
          }

        }

        line_em_up_to(p, documents.size(), response.max_score);

// old default
//        if (step > 0 && step <= documents.size()) {
//          int id = step;
//          line_em_up_to(p, id);
//        }
//        for (int i=step; i < documents.size(); i++) {
//          documents.get(i).draw(p);
//        }
        break;
    }

    super.draw(p, step);
  }

  private void reset_em() {
    documents.forEach(document -> {
      document.on = false;
      document.score = 0.0f;
    });
  }

  private void line_em_up_to(ProcessCene p, int up_to, float max_score) {
    for (int i = 0; i < up_to; i++) {
      DocumentAvatar d = documents.get(i);

      // compute final location
      int doc_x = (int) PApplet.map(i, 0, documents.size()-1, 50, p.width - 50);
      int doc_y = 600;

      // take a step that way
      int diff_x = doc_x - d.x;
      int diff_y = doc_y - d.y;
      int step_x = (Math.abs(diff_x) > 10 ? 10 : 1) * (diff_x == 0 ? 0 : (diff_x > 0 ? 1 : -1));
      int step_y = (Math.abs(diff_y) > 10 ? 10 : 1) * (diff_y == 0 ? 0 : (diff_y > 0 ? 1 : -1));
      d.x = d.x + step_x;
      d.y = d.y + step_y;
//      d.print();

      d.draw(p);

      if ((diff_x == 0) && (diff_y == 0) && (d.score > 0.0)) {
        float h = PApplet.map(d.score, 0, max_score, 0, 300);
        p.fill(138, 20);
        p.rect(d.x, d.y - h, 30, h);

        p.fill(p.theme.foreground);
        p.text(d.score,d.x,d.y - h);
      }

    }
  }

  private void circle_em(ProcessCene p) {
    p.push();

    float r = 200;

//    p.circle(p.width / 2, p.height / 2, r * 2);

    float degrees_per_doc = 360.0f / documents.size();
    for (int i = 0; i < documents.size(); i++) {
      DocumentAvatar d = documents.get(i);

      double radians = Math.toRadians(degrees_per_doc + p.frameCount % 360);
      d.x = (int) (p.width/2 + r * Math.cos(radians * (i+1)));
      d.y = (int) (p.height/2 + r * Math.sin(radians * (i+1)));

//      if (p.frameCount % d.id == 0) d.on = !d.on;
//      d.jiggle();

      d.draw(p);
    }

    p.pop();
  }
}
