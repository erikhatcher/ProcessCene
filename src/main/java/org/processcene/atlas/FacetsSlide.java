package org.processcene.atlas;

import com.mongodb.client.AggregateIterable;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.processcene.core.BaseSlide;
import org.processcene.core.ProcessCene;
import processing.core.PApplet;

import java.util.List;

public class FacetsSlide extends BaseSlide {
  private final List<Bson> facet_aggregation;
  private final AtlasAdapter atlas;
  private Document response = null;
  private long query_time = -1;

  public FacetsSlide(String title, AtlasAdapter atlas, List<Bson> facet_aggregation) {
    super(title);

    this.facet_aggregation = facet_aggregation;
    this.atlas = atlas;
  }

  @Override
  public void draw(ProcessCene p, int step) {
    if (response == null) {
      long start = System.currentTimeMillis();
      AggregateIterable<Document> aggregate = atlas.collection.aggregate(facet_aggregation);
      response = aggregate.iterator().next();
      long end = System.currentTimeMillis();
      query_time = end - start;
    }

    Document meta = (Document) response.get("meta");
    Long count = ((Document) meta.get("count")).getLong("lowerBound");
    Document facets = (Document) meta.get("facet");
    List<Document> year_facets = (List<Document>) ((Document) facets.get("year_facet")).get("buckets");
    List<Document> genre_facets = (List<Document>) ((Document) facets.get("genre_facet")).get("buckets");

//    {
//      "docs": [
//      {
//        "_id": {
//        "$oid": "573a1390f29313caabcd42e8"
//      },...
//  ],
//      "meta": {
//      "count": {
//        "lowerBound": {
//          "$numberLong": "21349"
//        }
//      },
//      "facet": {
//        "year_facet": {
//          "buckets": [
//          {
//            "_id": 1950,
//              "count": {
//            "$numberLong": "617"
//          }
//          },...
//        ]
//        }
//      }
//    }
//    }

    int x = 10;
    int y = 20;

    p.text("Count: " + count, x, y);
    y += 40;

    for (Document year_facet : year_facets) {
      Object year_bucket = year_facet.get("_id");
      Long num_per_year = year_facet.getLong("count");

      p.text(year_bucket + " (" + num_per_year + ")", x, y);
      p.rect(x, y, PApplet.map(num_per_year, 0, count, x, 900), 20);
      y += 40;
    }

    x = 900;
    y = 60;
    for (Document genre_facet : genre_facets) {
      Object genre_bucket = genre_facet.get("_id");
      Long num_per_genre = genre_facet.getLong("count");

      p.text(genre_bucket + " (" + num_per_genre + ")", x, y);
      p.rect(x, y, PApplet.map(num_per_genre, 0, count, 0, 900), 20);
      y += 40;
    }


    super.draw(p, step);
  }
}
