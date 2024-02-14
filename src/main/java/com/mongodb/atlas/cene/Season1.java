package com.mongodb.atlas.cene;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.search.SearchOperator;
import com.mongodb.client.model.search.SearchPath;
import org.bson.BsonArray;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.processcene.AllyzersSlide;
import org.processcene.QueryRunnerSlide;
import org.processcene.SimilaritySlide;
import org.processcene.TopDocsSlide;
import org.processcene.atlas.AtlasAdapter;
import org.processcene.atlas.AtlasSearchRequest;
import org.processcene.atlas.FacetsSlide;
import org.processcene.atlas.MongoTheme;
import org.processcene.core.BaseSlide;
import org.processcene.core.BulletPointsSlide;
import org.processcene.core.DocumentAvatar;
import org.processcene.core.LoveLuceneSlide;
import org.processcene.core.ProcessCene;
import org.processcene.core.SearchEngineAdapter;
import org.processcene.core.SearchRequest;
import org.processcene.core.Slide;
import org.processcene.core.SplashSlide;
import org.processcene.core.TableOfContentsSlide;
import org.processcene.core.Theme;
import org.processcene.lucene.InvertedIndexSlide;
import org.processcene.lucene.LuceneAnalyzer;
import processing.core.PImage;
import processing.event.MouseEvent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Season1 extends ProcessCene {
  @Override
  public void settings() {
    size(1920, 1080);
  }

  @Override
  public void setup() {
    Theme mongo_theme = new MongoTheme();
    setTheme(mongo_theme);
    window_title_prefix = "The Atlas Search 'cene: Season 1";
    show_footer = false;

    Slide love_lucene = new LoveLuceneSlide(false);
    PImage toc_bullet_image = loadImage(getFilePathFromResources("Assets/normal/General_ACTION_Favorite_Inverted10x.png"));
    toc_bullet_image.resize(50, 0);

    PImage cene_splash = loadImage(getFilePathFromResources("cene_splash.png"));
    cene_splash.resize(0,height);
    add(new SplashSlide("The Atlas Search 'cene", theme.background, cene_splash));

    BsonArray cast = new BsonArray();
    cast.add(new BsonString("Keanu Reeves"));
    Bson match = Aggregates.match(new Document("cast", new Document("$in", cast)));
    Bson sort = Aggregates.sort(new Document("imdb.rating", -1));
    Bson project = Aggregates.project(new Document("id", 1).append("cast", 1).append("title", 1));
    List<Bson> doc_selector = Arrays.asList(match, sort, project);
    AtlasAdapter atlas = new AtlasAdapter("sample_mflix", "movies", doc_selector);

    slides.add(new QueryRunnerSlide("Query Runner", atlas, new String[] {
        "purple rain", "keanu reeves", "prince", "bruce lee", "Jacky Chan", "martial arts movies"
    }));


    List<Slide> episode_outlines = new ArrayList<>();

    episode_outlines.add(new BulletPointsSlide("Episode 1: What is Atlas Search & Quick Start", new String[] {
        "What is Atlas",
        "  - Developer data platform",
        "  - Document model",
        "What is Atlas Search and how does it fit into the DDP",
        "  - Powerful document set/list selectors",
        "  - Findability: needles in haystacks",
        "  - Relevancy: best needles come to top",
        "Atlas UI",
        "  - add sample docs (or your own data!)",
        "  - enable search",
        "  - Search Tester",
        "Compass",
        "Docs / Links"
    }));

    episode_outlines.add(new BulletPointsSlide("Episode 2: Configuration / Development Environment", new String[] {
            "Compass pipeline to code",
            "Run Code",
            "Index configuration",
            "  - Language, facets, sorting, `multi`, synonyms",
            "Search Index commands",
            "  - Atlas CLI"
    }));

    episode_outlines.add(new BulletPointsSlide("Episode 3: Indexing", new String[] {
        "Findability is intimately tied to index configuration",
        "  - What you Index is What you Can Find",
        "Document model lends itself well to index structure",
        "  - (data) Documents —-to—> (index) Documents",
        "Automatic synchronization",
        "Change index configuration, everything reindexes"
    }));

    episode_outlines.add(new BulletPointsSlide("Episode 4: Searching", new String[] {
        "How index configuration matters for search",
        "Search operators",
        "  - Compound ftw",
        "  - full text: text, phrase, regex, wildcard",
        "Relevancy",
        "  - compound/should’s",
        "  - Score parameters",
        "  - \"match lots of ways, let relevancy sort it out\"",
        "Tracking for Query Analytics"
    }));

    episode_outlines.add(new BulletPointsSlide("Episode 5: Faceting", new String[] {
        "Concepts:",
        "  - Sets",
        "  - Buckets",
        "  - Labels",
        "In Action:",
        "  - Movies by decade",
        "  - Movies by genre"
    }));

    episode_outlines.add(new BulletPointsSlide("Episode 6: Advanced Search Topics", new String[] {
        "Embedded documents",
        "Fuzzy search",
        "Autocomplete",
        "Geospatial",
        "Highlighting",
        "More Like This"
    }));

    episode_outlines.add(new BulletPointsSlide("Episode 7: Query Analytics", new String[] {
        "Need an M10 or higher cluster",
        "Track Search Terms — MongoDB Atlas",
        "Zero Results equals Zero Dollars",
        "Taking action on analytics results",
        "Review top queries and result performances",
        "Improve recall and quality of top results",
        "Review and address zero results queries",
        "Adjust queries",
        "Add synonyms",
        "Adjust index configuration"
    }));

    episode_outlines.add(new BulletPointsSlide("Episode 8: Tips and Tricks", new String[] {
        "Look at explain() output, and scoreDetails",
        "'in' operator",
        "Combine search results and facets in a single request",
        "Model it how you want to find it",
        "  - match lots of ways, let relevancy sort it out"
    }));

//    add(love_lucene);
    // Episode ToC and Episode tracking slides before each section
    Slide season_guide = new TableOfContentsSlide(episode_outlines,toc_bullet_image);
    add(season_guide);
    for (int i = 0; i < episode_outlines.size(); i++) {
      int episode_number = i + 1;
      Slide episode_outline = episode_outlines.get(i);

      // For each episode, add its own table of contents, highlighted, then the episode outline,
      //   ... followed by slides specific to each episode
      slides.add(new TableOfContentsSlide(episode_outlines,toc_bullet_image, i));
      slides.add(episode_outline);

      switch(episode_number) {
        case 1:
          // What is Atlas Search and Quick Start
          PImage s1e1_ddp_image = loadImage(getFilePathFromResources("S1E1-DDP.png"));
          s1e1_ddp_image.resize(1300,0);
          PImage s1e1_document_model_image = loadImage(getFilePathFromResources("S1E1_DocumentDataModel.png"));
          s1e1_document_model_image.resize(0,700);
          PImage s1e1_as_features_image = loadImage(getFilePathFromResources("S1E1-AtlasSearchCapabilities.png"));
          s1e1_as_features_image.resize(1300,0);
          PImage s1e1_as_architecture_image = loadImage(getFilePathFromResources("S1E1_AtlasSearchArchitecture.png"));
          s1e1_as_architecture_image.resize(0,700);
          add(new SplashSlide("What is Atlas", theme.background,
              new PImage[] {s1e1_ddp_image, s1e1_document_model_image, s1e1_as_architecture_image}, null));
          add(love_lucene);
          add(episode_outline);
          add(new BulletPointsSlide("Quick Start", new String[] {
              "Atlas UI",
              "  - add sample docs",
              "  - enable search",
              "  - Search Tester",}));
          break;

        case 2:
          // Configuration / Development Environment
          break;

        case 3:
          // Indexing
//          add(love_lucene);
          List<DocumentAvatar> docs = new ArrayList<>();

          Map<String, Object> doc = new HashMap<>();
          doc.put("id",1);
          doc.put("type","doc");
          doc.put("title","MongoDB Day: Adobe Lehi");
          docs.add(new DocumentAvatar(doc));
          doc = new HashMap<>();
          doc.put("id",2);
          doc.put("type","doc");
          doc.put("title","Topics: Full-text Search and Vector Search");
          docs.add(new DocumentAvatar(doc));
          doc = new HashMap<>();
          doc.put("id",3);
          doc.put("type","doc");
          doc.put("title","Atlas Search at Adobe!");
          docs.add(new DocumentAvatar(doc));
          add(new InvertedIndexSlide("Inverted Index", docs, "Standard"));

          add(new AllyzersSlide("Analysis Overview", new LuceneAnalyzer(), "Here's some text"));

          break;

        case 4:
          // Searching
          //add(love_lucene);

          add(new AllyzersSlide("Analysis Refresher", new LuceneAnalyzer(), "Bill & Ted's Excellent Adventure"));

          SearchOperator search_operator = SearchOperator.of(new Document("compound",
              new Document("filter", Arrays.asList(new Document("text",
                  new Document("path", "cast")
                      .append("query", "keanu"))))
                  .append("must", Arrays.asList(new Document("text",
                      new Document("query", "ADVENTUROUS JoUrneyS")
                          .append("path",
                              new Document("value", "title")
                                  .append("multi", "english"))))))
          );
          List<SearchRequest> search_requests = new ArrayList<>();
          search_requests.add(
              new AtlasSearchRequest("ADVENTUROUS JoUrneyS", "ADVENTUROUS JoUrneyS", search_operator));
          add(new TopDocsSlide("Top Docs", atlas, search_requests));

          add(new SimilaritySlide("Similarity"));

          break;

        case 5:
          // Faceting
//          add(love_lucene);
          add(new FacetsSlide("Facets", atlas, get_facet_aggregation()));
          break;

        case 6:
          // Advanced Search Topics
          add(love_lucene);
          break;

        case 7:
          // Query Analytics
          break;

        case 8:
          // Tips and Tricks
          break;
      }

      add(new SplashSlide("End of Episode " + episode_number, theme.background, null));
    }

//    add(new TempSlide());

    super.setup();  // super.setup() calls #init on each slide
  }

    private List<Bson> get_facet_aggregation() {
      return Arrays.asList(new Document("$search",
              new Document("facet",
                  new Document("operator",
                      new Document("phrase",
                          new Document("path", "cast")
                              .append("query", "keanu reeves")))
                      .append("facets",
                          new Document("year_facet",
                              new Document("type", "number")
                                  .append("path", "year")
                                  .append("boundaries", Arrays.asList(1920L, 1930L, 1940L, 1950L, 1960L, 1970L, 1980L, 1990L, 2000L, 2010L, 2020L, 2030L))
                                  .append("default", "other"))
                              .append("genre_facet",
                                  new Document("type", "string")
                                      .append("path", "genres"))))),
          new Document("$facet",
              new Document("docs", Arrays.asList(new Document("$limit", 10L),
                  new Document("$project",
                      new Document("title", 1L)
                          .append("released", 1L))))
                  .append("meta", Arrays.asList(new Document("$replaceWith", "$$SEARCH_META"),
                      new Document("$limit", 1L)))),
          new Document("$set",
              new Document("meta",
                  new Document("$arrayElemAt", Arrays.asList("$meta", 0L)))));
  }

  public static void main(String[] args) {
    ProcessCene.run(Season1.class.getName());
  }

  private class TempSlide extends BaseSlide {
    public TempSlide() {
      super("temp");
    }

    @Override
    public void mouseClicked(MouseEvent event) {
      System.out.println("event = " + event);

      try {
        Desktop.getDesktop().browse(new URI("https://mdb.link/erik"));
      } catch (IOException e) {
        throw new RuntimeException(e);
      } catch (URISyntaxException e) {
        throw new RuntimeException(e);
      }

    }

    @Override
    public void draw(ProcessCene p, int step) {
      p.text("click me", 50,50);

      super.draw(p, step);
    }
  }
}
