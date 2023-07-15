import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.search.SearchOperator;
import com.mongodb.client.model.search.SearchOptions;
import org.bson.BsonBoolean;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.search;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Projections.meta;
import static com.mongodb.client.model.Projections.metaSearchScore;
import static com.mongodb.client.model.search.SearchPath.fieldPath;

public class AtlasSearchQueryingSlide extends BaseSlide {
  public AtlasSearchQueryingSlide(String title, ProcessCene presentation) {
    super(title, presentation);

    // Replace the placeholder with your MongoDB deployment's connection string
    String uri = System.getenv("ATLAS_URI");

    try (MongoClient mongoClient = MongoClients.create(uri)) {
      // set namespace
      MongoDatabase database = mongoClient.getDatabase("sample_mflix");
      MongoCollection<Document> collection = database.getCollection("movies");

      SearchOperator genresClause = SearchOperator.compound()
          .must(Arrays.asList(
              SearchOperator.text(fieldPath("genres"),"Drama"),
              SearchOperator.text(fieldPath("genres"), "Romance")
          ));

      List<SearchOperator> filters = List.of(genresClause);

      Document searchQuery = new Document("phrase",
          new Document("query", "keanu reeves")
              .append("path", fieldPath("cast"))
              .append("slop",2));

      Bson searchStage = search(
          SearchOperator.compound()
              .filter(filters)
              .must(List.of(SearchOperator.of(searchQuery))),
          SearchOptions.searchOptions().option("scoreDetails", BsonBoolean.TRUE)
      );

      // Create a pipeline that searches, projects, and limits the number of results returned.
      AggregateIterable<Document> aggregationSpec = collection.aggregate(Arrays.asList(
          searchStage,
          project(fields(excludeId(),
              include("title", "cast", "genres"),
              metaSearchScore("score"),
              meta("scoreDetails", "searchScoreDetails"))),
          limit(30)));

      // Print out each returned result
      //aggregation_spec.forEach(doc -> System.out.println(formatJSON(doc)));
      aggregationSpec.forEach(doc -> {
        System.out.println(doc.get("title"));
        System.out.println("  Cast: " + doc.get("cast"));
        System.out.println("  Genres: " + doc.get("genres"));
        System.out.println("  Score:" + doc.get("score"));
//        printScoreDetails(2, doc.toBsonDocument().getDocument("scoreDetails"));
        System.out.println();
      });

      // Print the explain output, which shows query interpretation details
//      System.out.println("Explain:");
//      System.out.println(format(aggregationSpec.explain().toBsonDocument()));
    } catch (Exception e) {
      System.err.println(e.getLocalizedMessage());
    }

  }

  @Override
  public void draw(int step) {
    presentation.background(255,255,255);
    presentation.fill(0,0,0);
    presentation.textSize(14);
    float x = 20;
    float y = presentation.textAscent() + presentation.textDescent() + 10;

    presentation.text("Atlas Search!!!", x,7);
    y += 20;

    super.draw(step);
  }
}
