package org.processcene;

import org.processcene.core.BaseSlide;
import org.processcene.core.DocumentAvatar;
import org.processcene.core.ProcessCene;
import org.processcene.core.ResponseDocument;
import org.processcene.core.SearchEngineAdapter;
import org.processcene.core.SearchRequest;
import org.processcene.core.SearchResponse;
import processing.core.PApplet;
import processing.event.MouseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class TopDocsSlide extends BaseSlide {
  private ProcessCene p;
  private List<DocumentAvatar> documents = new ArrayList<>();
  private SearchEngineAdapter engine;

  Map<SearchRequest, SearchResponse> response_cache = new HashMap<>();
  private List<ResponseDocument> current_results;
  private int show_explain_for = 0;
  private int last_step = -1;
  private List<SearchRequest> search_requests;

  public TopDocsSlide(String title, SearchEngineAdapter engine) {
    super(title);
    this.engine = engine;
  }

  public TopDocsSlide(String title, SearchEngineAdapter engine, List<SearchRequest> search_requests) {
    this(title, engine);
    this.search_requests = search_requests;
  }

  @Override
  public void mouseClicked(MouseEvent event) {
    show_explain_for = 0;

//    System.out.println("event = " + event);
//    System.out.println("event.getX() = " + event.getX());
//    System.out.println("event.getY() = " + event.getY());

    for (int i = 0; i < current_results.size(); i++) {
      ResponseDocument result = current_results.get(i);
      DocumentAvatar da = documents.get(result.id - 1);

      int diff_x = Math.abs(event.getX() - da.x);
      int diff_y = Math.abs(event.getY() - da.y);

      if (diff_x < 20 && diff_y < 20) {
        show_explain_for = result.id;
//        System.out.println("Clicked on: " + da.title);
//        System.out.println("da.explain = " + da.explain);
        break;
      }
    }
  }

  @Override
  public void init(ProcessCene p) {
    super.init(p);

    this.p = p;

    documents = engine.getDocuments();

    for (int i = 0; i < documents.size(); i++) {
      DocumentAvatar document = documents.get(i);
      int x = ThreadLocalRandom.current().nextInt(0, p.width + 1); // PApplet.map(i, 0, max, 0, p.width);
      int y = ThreadLocalRandom.current().nextInt(0, p.height + 1);
      document.x = x;
      document.y = y;
      document.home_x = x;
      document.home_y = y;
//      document.on = (i % 1000 == 0) ? true : false; // ThreadLocalRandom.current().nextInt(0,2) != 1 ? true : false;
    }
  }


  @Override
  public int getNumberOfSteps() {
    return search_requests.size();
  }

  @Override
  public void draw(ProcessCene p, int step) {
    if (last_step != step) {
      // if had been showing explain, turn it off
      show_explain_for = 0;
      last_step = step;
    }

    for (int i = 0; i < documents.size(); i++) {
      // reset each avatar
      DocumentAvatar da = documents.get(i);
      da.on = false;
      da.position = 0;
      da.score = 0.0f;
      da.explain = null;
    }

    float max_score = 0f;
    if (step > 0) {
      int query_index = step - 1;
      // get next query
      // search
      // turn on/off the avatars

      // TODO: !! - cache results so every draw() isn't doing a search
      SearchRequest request = search_requests.get(query_index);
      SearchResponse response = response_cache.get(request);
      if (response == null) {
        response = engine.search(request);
        response_cache.put(request, response);
      }

      max_score = response.max_score;

      if (response.error != null) {
        p.fill(p.theme.error_color);
        p.text(response.error, 50, 50);
      }

      current_results = response.documents;

      // light up the avatars related to our search results
      for (int i = 0; i < current_results.size(); i++) {
        ResponseDocument result = current_results.get(i);
        DocumentAvatar da = documents.get(result.id - 1);
        da.on = true;
        da.position = i + 1;
        da.score = result.score;
        da.explain = result.explain;
      }

      p.text(request.getLabel(), 30, 30);
      if (response.documents.size() == 0) {
        p.text("NO DOCUMENTS FOUND", 50, 80);
      }

      // TODO: make a key to turn on/off this d)ebug info
      p.text(request.toString(), 1100, 20);
      String query_time_string = response.query_time + "ms";
      p.text(query_time_string, p.width - p.textWidth(query_time_string), 650);
      p.text(response.explain == null ? "null explain()" : response.explain, 10, 700);
      //  p.text(response.parsed_query, 30, 50);
    }

    for (int i = 0; i < documents.size(); i++) {
      DocumentAvatar doc = documents.get(i);
      switch (step) {
        case 0:
//          String t = getTitle();
//          p.text(t, (p.width - p.textWidth(t)) / 2, (p.height - p.textAscent() - p.textDescent()) / 2);
          doc.x = doc.home_x;
          doc.y = doc.home_y;
          doc.jiggle();
          doc.draw_as_doc(p);
          break;

        default:
          if (doc.on) {
            int target_x = 10;
            int target_y = doc.position * 55 + 80;
            // Document is a match, it's on!
            // find our home position, by our ranking in the results
            if (step > 1) { // after first animation, warp to results
              doc.x = target_x;
              doc.y = target_y;
            } else {

              // move towards our target position
              if (doc.y > target_y) {
                doc.y -= 8; // (8-match_index)*2;
              } else {
                doc.y = target_y;
              }

              if (doc.x > target_x) {
                doc.x -= 8; // (8-match_index)*2;
              } else {
                doc.x = target_x;
              }
            }

            if (doc.x == target_x && doc.y == target_y) {
              // Home sweet home!
              float score_width = PApplet.map(doc.score, 0, max_score, 0, 1000);

              //p.strokeWeight(2.0f);
              p.noStroke();

              p.fill(p.theme.error_color, 30);
              p.rect(doc.x + 50, doc.y, doc.x + score_width, 30);

              p.fill(p.theme.foreground);
              p.text(doc.title, doc.x, doc.y);
              p.text("" + doc.score, doc.x + 50 + score_width - p.textWidth("" + doc.score), doc.y + p.textAscent() + p.textDescent());

            } else {
              p.text(doc.id, doc.x, doc.y);
            }
            doc.draw_as_doc(p);

            if (show_explain_for != 0) {
              DocumentAvatar da = documents.get(show_explain_for - 1);

              p.fill(p.theme.color_by_name("lavender"));
              p.rect(100, 75, p.width, p.height);

              p.fill(p.theme.foreground);
              p.stroke(p.theme.foreground);
              p.text(da.title + ":\n" + da.explain, 100, 75 + p.textAscent() + p.textDescent());
            }

          } else {
            // Not a match, drop to the bottom and disappear
            if (step > 1) {
              doc.y = p.height;
            } else {
              if (doc.y < p.height) {
                doc.y += 8;
                doc.jiggle();
                doc.draw_as_doc(p);
              }
            }
          }

          break;
      }
    }

    super.draw(p, step);
  }
}
