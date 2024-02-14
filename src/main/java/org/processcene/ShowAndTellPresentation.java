package org.processcene;

import org.processcene.atlas.AtlasAdapter;
import org.processcene.atlas.AtlasSearchOnSolrTagger;
import org.processcene.atlas.MongoTheme;
import org.processcene.core.BulletPointsSlide;
import org.processcene.core.DocumentAvatar;
import org.processcene.core.LoveLuceneSlide;
import org.processcene.core.ProcessCene;
import org.processcene.core.SearchRequest;
import org.processcene.core.SplashSlide;
import org.processcene.core.TableOfContentsSlide;
import org.processcene.core.Theme;
import org.processcene.lucene.InvertedIndexSlide;
import org.processcene.lucene.LuceneAnalyzer;
import org.processcene.lucene.QueryParsingSlide;
import org.processcene.lucene.VectorSearchSlide;
import org.processcene.solr.SolrTaggerSlide;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowAndTellPresentation extends ProcessCene {

  @Override
  public void setup() {
    Theme mongo_theme = new MongoTheme();
    setTheme(mongo_theme);
    window_title_prefix = "Show And Tell";
    AtlasAdapter atlas = new AtlasAdapter("sample_mflix", "movies");

    add(new SplashSlide("Atlas Search", theme.color_by_name("lavender"),
        loadImage(getFilePathFromResources("mongodb-assets/Search/Technical_ATLAS_Search10x.png"))).setShowOnTOC(false));

    add(new BulletPointsSlide("Show and Tell Outline",
        new String[]{"Show", "Tell"}).setShowOnTOC(false));

    add(new InvertedIndexSlide("Inverted Index: Standard analyzer", demo_docs(), "Standard"));
    add(new InvertedIndexSlide("Inverted Index: English analyzer", demo_docs(), "English"));

    String sample_text = "Let's Analyze: AtlasSearch";
    TextAnalyzer text_analyzer = new LuceneAnalyzer();
    add(new AnalysisSlide("Analysis", text_analyzer, sample_text));

    add(new AllyzersSlide("Analysis Overview", text_analyzer, sample_text));
    add(new QueryParsingSlide("Query Parsing"));
    add(new SimilaritySlide("Similarity"));
//    add(new VectorSearchSlide("Vector Search"));

    Map<String, List<SearchRequest>> search_request_sets = load_search_request_sets("atlas/relevancy_evolution.json");
    add(new TopDocsSlide("Top Docs: purple rain", atlas, search_request_sets.get("purple rain")));
    add(new TopDocsSlide("Top Docs: the purple rain", atlas, search_request_sets.get("the purple rain")));

    List<String> geo_texts = new ArrayList<>();
    geo_texts.add("Hello, from Charlottesville!");
    add(new SolrTaggerSlide("Solr Tagger on geonames", "geonames", geo_texts));

    List<String> movie_texts = new ArrayList<>();
    movie_texts.add("Drama movies with Keanu Reeves");
    add(new SolrTaggerSlide("Solr Tagger on Movies", "tagger", movie_texts));

    add(new AtlasSearchOnSolrTagger("Atlas Search on Solr Tagger", atlas));

    add(new LoveLuceneSlide());

    PImage toc_bullet_image = loadImage(getFilePathFromResources("Assets/normal/General_ACTION_Favorite_Inverted10x.png"));
    toc_bullet_image.resize(50, 0);
    slides.add(2, new TableOfContentsSlide(slides, toc_bullet_image));

    super.setup();
  }

  private List<DocumentAvatar> demo_docs() {
    List<DocumentAvatar> some_docs = new ArrayList<>();
    Map<String, Object> d = new HashMap<>();
    d.put("id", 1);
    d.put("type", "doc");
    d.put("title", "Show and Tell Document 1");
    DocumentAvatar a = new DocumentAvatar(d);
    a.on = true;
    some_docs.add(a);

    d.clear();
    d.put("id", 2);
    d.put("type", "doc");
    d.put("title", "Show and Tell Document 2");
    a = new DocumentAvatar(d);
    a.on = true;
    some_docs.add(a);

    d.clear();
    d.put("id", 3);
    d.put("type", "doc");
    d.put("title", "DR: Show and Tell Session");
    a = new DocumentAvatar(d);
    a.on = true;
    some_docs.add(a);

    d.clear();
    d.put("id", 4);
    d.put("type", "doc");
    d.put("title", "Showing and Telling: ProcessCene");
    a = new DocumentAvatar(d);
    a.on = true;
    some_docs.add(a);

    return some_docs;
  }

  public static void main(String[] args) {
    ProcessCene.run(ShowAndTellPresentation.class.getName());
  }

}
