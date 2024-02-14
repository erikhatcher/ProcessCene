package org.processcene;

import com.mongodb.client.model.Aggregates;
import org.bson.BsonArray;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.processcene.atlas.AtlasAdapter;
import org.processcene.atlas.FacetsSlide;
import org.processcene.atlas.MongoTheme;
import org.processcene.core.BulletPointsSlide;
import org.processcene.core.ProcessCene;
import org.processcene.core.SearchRequest;
import org.processcene.core.SplashSlide;
import org.processcene.core.Theme;
import processing.core.PImage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RedMonkPresentation extends ProcessCene {
//  private final SearchEngineAdapter engine;

  // This doc_selector can be used to narrow the scope of documents considered for this presentation
  private List<Bson> doc_selector() {
    BsonArray cast = new BsonArray();
    cast.add(new BsonString("Keanu Reeves"));
//    cast.add(new BsonString("Christopher Reeve"));
    Bson match = Aggregates.match(new Document("cast", new Document("$in", cast)));
    Bson sort = Aggregates.sort(new Document("imdb.rating", -1));

    Bson project = Aggregates.project(new Document("id", 1).append("cast", 1).append("title", 1));

    return Arrays.asList(match, sort, project);
  }

//  @Override
//  public void settings() {
//    fullScreen();
//  }

  @Override
  public void setup() {
    Theme mongo_theme = new MongoTheme();
    setTheme(mongo_theme);
    window_title_prefix = "RedMonk: What is Atlas Search? How to help users find what they need...";

    AtlasAdapter atlas = new AtlasAdapter("sample_mflix", "movies");

    PImage redmonk_icon = loadImage(getFilePathFromResources("redmonk_icon.png"));
    add(new SplashSlide("RedMonk", theme.color_by_name("lavender"), redmonk_icon).setShowOnTOC(false));

//    PImage redmonk_logo = loadImage(getFilePathFromResources("redmonk_logo.jpeg"));
//    add(new SplashSlide("RedMonk", theme.color_by_name("lavender"), redmonk_logo).setShowOnTOC(false));

    Map<String, List<SearchRequest>> search_request_sets = load_search_request_sets("atlas/redmonk.json");

    String[] query_sets = {
        "red monk"
    };

    add(new BulletPointsSlide("q = ", query_sets));
    for (String query_string : query_sets) {
      // query_string is the key in the redmonk.json file Document
      add(new TopDocsSlide("q = " + query_string, atlas, search_request_sets.get(query_string)));
    }

    add(new FacetsSlide("Movie Facets: All", atlas, get_facet_aggregation_all()));

    add(new FacetsSlide("Movie Facets: cast:\"keanu reeves\"", atlas, get_facet_aggregation_keanu()));

    add(new QueryRunnerSlide("Query Runner", atlas, new String[]{
        "wick","lodr of the rinsg", "action adventure movies","enter the dragon"
    }));

    super.setup();
  }

  private List<Bson> get_facet_aggregation_all() {
    return Arrays.asList(new Document("$search",
            new Document("facet",
                new Document("facets",
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

  private List<Bson> get_facet_aggregation_keanu() {
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
    ProcessCene.run(RedMonkPresentation.class.getName());
  }
}
