package org.processcene;

import org.processcene.atlas.AtlasAdapter;
import org.processcene.atlas.AtlasSearchOnSolrTagger;
import org.processcene.atlas.MongoTheme;
import org.processcene.core.BulletPointsSlide;
import org.processcene.core.LoveLuceneSlide;
import org.processcene.core.ProcessCene;
import org.processcene.core.SplashSlide;
import org.processcene.core.TableOfContentsSlide;
import org.processcene.core.Theme;
import org.processcene.solr.SolrAdapter;
import org.processcene.solr.SolrTaggerSlide;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;


public class SearchTeamSolr extends ProcessCene {
  @Override
  public void setup() {
    Theme mongo_theme = new MongoTheme();
    setTheme(mongo_theme);
    window_title_prefix = "Search Team";

    PImage solr_logo = loadImage(getFilePathFromResources("Solr_Logo_on_white.png"));
    solr_logo.resize(0, 150);

    add(new SplashSlide("Solr: What's Awesome About It", theme.background, solr_logo).setShowOnTOC(false));

    add(new BulletPointsSlide("Why...?", new String[]{
        "Why talk about Solr here?",
        "   1) Competition*",
        "   2) Inspiration!",
        "   3) The {code} (ASL 2.0)"
    }));

    add(new BulletPointsSlide("Solr Power", new String[]{
        "Demo: Quick Start",
        "Demo: Movies Data",
        "Terminology: collection, shard, replica, core, index",
        "Analysis: APIs, UI, WordDelimiterFilter",
        "Search API: parameter substitution, template substitution, query parser syntax",
        "Query parsing: plethora of parsers, {!terms}, {!graph}, edismax...",
        "\"Aggregations\": facets, collapsing, grouping, JSON Query API, streaming expressions",
        "Joins: intra-index, cross-core, cross-collection",
        "Solr Tagger",
    }));

    add(new SplashSlide("Quick Start Demo", theme.background, solr_logo).setShowOnTOC(false));

    add(new TopDocsSlide("Solr Powered Movies",
        new SolrAdapter("http://localhost:8983/solr", "movies")).setShowOnTOC(false));

    add(new SplashSlide("Solr Tagger Demos", theme.background, solr_logo).setShowOnTOC(false));

    List<String> geo_texts = new ArrayList<>();
    geo_texts.add("Good Morning, san francisco!");
    add(new SolrTaggerSlide("Solr Tagger on geonames", "geonames", geo_texts).setShowOnTOC(false));

    List<String> movie_texts = new ArrayList<>();
    movie_texts.add("Have you see The Purple Rain?");
    movie_texts.add("Falling Down?  Or Falling Up?");
    movie_texts.add("Have you see The Deep Purple Rain?");
    movie_texts.add("Joe likes \"tears in the rain\" quote from the original blade runner");
    add(new SolrTaggerSlide("Solr Tagger on movies", "tagger", movie_texts).setShowOnTOC(false));

    add(new BulletPointsSlide("Ideas", new String[]{
        "Yours?"
    }));
    add(new BulletPointsSlide("Ideas", new String[]{
        "Mine:",
        "  * Tagging: content entity tagging during indexing",
        "  * Tagging: query pre-processor + $search",
        "  * `queryString` Evolution"
    }).setShowOnTOC(false));

    add(new AtlasSearchOnSolrTagger("Atlas Search on the Solr Tagger",
        new AtlasAdapter("sample_mflix", "movies")).setShowOnTOC(false));

    add(new LoveLuceneSlide());

//    add(new DocumentsSlide("Atlas => Lucene / queryString PoC", new AtlasToLuceneAdapter(engine)));

//    add(new DocumentsSlide("Demo Documents", engine));
//
//    add(new InvertedIndexSlide("Inverted Index", engine.getDocuments()));
//    add(new SimilaritySlide("Similarity"));


    // Inject a Table of Contents slide at the beginning, showing every Slide set to show_on_toc
    // TODO: extract the ToC slide inject to base class or helper method
    PImage toc_bullet_image = loadImage(getFilePathFromResources("Assets/normal/General_ACTION_Favorite_Inverted10x.png"));
    toc_bullet_image.resize(50, 0);
    TableOfContentsSlide toc_slide = new TableOfContentsSlide(slides, toc_bullet_image);
    slides.add(1, toc_slide); // after the first (splash/welcome, in this case) slide

    super.setup();
  }

  public static void main(String[] args) {
    ProcessCene.run(SearchTeamSolr.class.getName());
  }
}
