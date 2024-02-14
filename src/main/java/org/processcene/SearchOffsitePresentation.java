package org.processcene;

import com.mongodb.client.model.Aggregates;
import org.bson.BsonArray;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.processcene.atlas.AtlasAdapter;
import org.processcene.atlas.MongoTheme;
import org.processcene.core.BulletPointsSlide;
import org.processcene.core.ProcessCene;
import org.processcene.core.SearchRequest;
import org.processcene.core.SplashSlide;
import org.processcene.core.Theme;
import org.processcene.lucene.LuceneAnalyzer;
import processing.core.PImage;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SearchOffsitePresentation extends ProcessCene {
//  private final SearchEngineAdapter engine;

  private List<Bson> doc_selector() {
    BsonArray cast = new BsonArray();
    cast.add(new BsonString("Keanu Reeves"));
//    cast.add(new BsonString("Christopher Reeve"));
    Bson match = Aggregates.match(new Document("cast", new Document("$in", cast)));
    Bson sort = Aggregates.sort(new Document("imdb.rating", -1));

    Bson project = Aggregates.project(new Document("id", 1).append("cast", 1).append("title", 1));

    return Arrays.asList(match, sort, project);
  }


  @Override
  public void setup() {
    Theme mongo_theme = new MongoTheme();
    setTheme(mongo_theme);
    window_title_prefix = "Relevancy Challenges";

    AtlasAdapter atlas = new AtlasAdapter("sample_mflix", "movies");
//    SearchEngineAdapter atlas_data_in_lucene = new AtlasToLuceneAdapter(atlas);
///    SearchEngineAdapter solr = new SolrAdapter("http://localhost:8983/solr", "movies");

    PImage search_logo = loadImage(getFilePathFromResources("mongodb-assets/Search/Technical_ATLAS_Search10x.png"));
    PImage search_bar = loadImage(getFilePathFromResources("mongodb-assets/Search(2)/Technical_SOFTWARE_Search(2)_Thumbnail_BS_ForestGreen.png"));

    add(new SplashSlide("Atlas Search", theme.color_by_name("lavender"), search_logo).setShowOnTOC(false));
//    AtlasAdapter limited_data_atlas = new AtlasAdapter("sample_mflix", "movies", doc_selector());
//    add(new DocumentsSlide("Atlas Queries", limited_data_atlas).setShowOnTOC(false));

    Map<String, List<SearchRequest>> search_request_sets = load_search_request_sets("atlas/relevancy_evolution.json");

    add(new AllyzersSlide("all", new LuceneAnalyzer(), "Some $search stuff"));

    add(new BulletPointsSlide("AtlasAdapter Stats", new String[]{
        "database: " + atlas.database.getName(),
        "collection: " + atlas.collection.getNamespace().getCollectionName(),
        "collection.countDocuments(): " + atlas.collection.countDocuments(),
        "doc_selector: " + atlas.subset_filter
    }));

    String[] query_strings = {"purple rain",
        "the purple rain" //,
//                               "falling down",
//                               "michael douglas falling down",
//                               "no time for sargents"
        //"samuel l jackson"
    };

    add(new BulletPointsSlide("q = ", query_strings));
    for (String query_string : query_strings) {
      // query_string is the key in the relevancy_evolution.json file Document
      add(new TopDocsSlide(query_string, atlas, search_request_sets.get(query_string)));
    }

    add(new SplashSlide("Atlas Search Query", theme.color_by_name("lavender"), search_bar));


//    add(new TopDocsSlide("purple rain", atlas, search_request_sets.get("purple rain")));
//    add(new TopDocsSlide("the purple rain", atlas, search_request_sets.get("the purple rain")));
//    add(new TopDocsSlide("falling down", atlas, search_request_sets.get("falling down")));
//    add(new TopDocsSlide("michael douglas falling down", atlas, search_request_sets.get("michael douglas falling down")));


//    add(new TopDocsSlide("Solr Powered Movies", solr));
//    add(new TopDocsSlide("Atlas data in Lucene", atlas_data_in_lucene));

//    add(new AtlasSearchOnSolrTagger("Atlas Search on the Solr Tagger", atlas));

    // Inject a Table of Contents slide at the beginning, showing every Slide set to show_on_toc
    // TODO: extract the ToC slide inject to base class or helper method
//    PImage toc_bullet_image = loadImage(getFilePathFromResources("Assets/normal/General_ACTION_Favorite_Inverted10x.png"));
//    toc_bullet_image.resize(50, 0);
//    TableOfContentsSlide toc_slide = new TableOfContentsSlide(slides, toc_bullet_image);
//    slides.add(2, toc_slide);  // after the two initial splash pages

    super.setup();
  }

  public static void main(String[] args) {
    ProcessCene.run(SearchOffsitePresentation.class.getName());
  }
}



/*
//    PImage lucene_in_action_cover = loadImage(getFilePathFromResources("lucene_in_action.png"));
//    lucene_in_action_cover.resize(0, 600);

//    PImage apache_feather = loadImage(getFilePathFromResources("apache_feather.png"));
//    apache_feather.resize(250,0);

//    PImage blacklight_logo = loadImage(getFilePathFromResources("blacklight_logo.png"));

//    PImage lucidworks_logo = loadImage(getFilePathFromResources("lucidworks_logo.png"));
//    lucidworks_logo.resize(500,0);


//    PImage lucene_logo = loadImage(getFilePathFromResources("lucene_green_300.png"));
//    lucene_logo.resize(300,0);
//    PImage solr_logo = loadImage(getFilePathFromResources("Solr_Logo_on_white.png"));
//    solr_logo.resize(0, 150);

//
//    slides.add(new org.processcene.AnalysisSlide("Text Analysis", text_analyzer, text, this));
//      slides.add(new org.processcene.AllyzersSlide("Analyzers", text_analyzer, text, this).setShowOnTOC(false));
//      slides.add(new org.processcene.core.BulletPointsSlide("Language Capabilities",
//          new String[] { "i18n / ICU / character folding", "word decompounding", "stemming", "phonetic"}, bullet_image, null, this).setShowOnTOC(false));
//
//    slides.add(new org.processcene.core.BulletPointsSlide("Other Index Types",
//        new String[] { "Numeric", "Spatial", "FST: Finite State Transducer", "Vector" }, bullet_image, null,this));
//
//    slides.add(new org.processcene.core.BulletPointsSlide("Query building",
//        new String[] { "Query API", "query parsing", "query intent (see Solr Tagger)" }, bullet_image, null, this));
    slides.add(new QueryParsingSlide("Query Parsing").setShowOnTOC(false));
//
//    slides.add(new org.processcene.core.BulletPointsSlide("Searching",
//        new String[] {"Filtering", "Relevancy Scoring"}, bullet_image, null, this));
//      slides.add(new org.processcene.core.BulletPointsSlide("Filtering",
//          new String[] { "non-scoring", "efficient skipping", "caching"}, bullet_image, null, this).setShowOnTOC(false));
//      slides.add(new org.processcene.core.BulletPointsSlide("Relevancy Scoring",
//          new String[] {"TF/IDF", "BM25", "Function boosting"}, bullet_image, null, this).setShowOnTOC(false));
//
//    slides.add(new org.processcene.core.BulletPointsSlide("Core Lucene Ecosystem",
//        new String[] { "Highlighting", "Suggest", "Spatial", "Facets", "Lucene Monitor", "MLT",
//            "Expressions", "Grouping", "Block join, parent/child", "Luke", "Vector Search"}, bullet_image, lucene_logo, this));
//      slides.add(new org.processcene.lucene.VectorSearchSlide("Vector Search", this).setShowOnTOC(false));
//
//    slides.add(new org.processcene.core.BulletPointsSlide("Lucene Inside",
//        new String[] {"Solr", "elasticsearch", "OpenSearch", "Lucidworks", "Atlas Search", "..."}, bullet_image, null,this));
//      slides.add(new org.processcene.core.BulletPointsSlide("Solr",
//          new String[] {"Tagger",
//                        "streaming expressions",
//                        "joins",
//                        "robust query parsing flexibility",
//                        "authentication / authorization",
//                        "extensible",
//                        "plugins: rich documents, etc"},
//          bullet_image, solr_logo, this).setShowOnTOC(false));
      slides.add(new org.processcene.solr.SolrTaggerSlide("Solr Tagger"));
//      slides.add(new org.processcene.core.BulletPointsSlide("Atlas Search", mist,
//          new String[] {"Click, click, click, it's real easy!",
//                        "Hosted",
//                        "Replicated",
//                        "Keeps database and search in sync",
//                        "Supports nested/embedded documents",
//                        "Flexible field type handling",
//                        "https://www.mongodb.com/atlas/search"}, bullet_image, atlas_search_logo ,this).setShowOnTOC(false));
//      slides.add(new org.processcene.core.SplashSlide("Atlas Search", mist,
//          new PImage[] {atlas_big_picture, atlas_search_ui_1, atlas_search_ui_2, atlas_search_ui_3, atlas_search_ui_4, atlas_search_ui_5, atlas_search_ui_6, atlas_search_ui_7, atlas_search_ui_8, atlas_search_ui_9, atlas_search_ui_10, atlas_search_ui_11},
//          "Atlas Search", this).setShowOnTOC(false));
//      slides.add(new org.processcene.atlas.AtlasSearchQueryingSlide("Atlas Search: Querying", this).setShowOnTOC(false));
//
//    slides.add(new org.processcene.core.SplashSlide("Go forth and search...", white, qr_code, "https://mdb.link/uberconf", this));

 */