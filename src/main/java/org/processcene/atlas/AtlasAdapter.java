package org.processcene.atlas;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.search.SearchOperator;
import com.mongodb.client.model.search.SearchOptions;
import com.mongodb.client.model.search.SearchPath;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.processcene.core.DocumentAvatar;
import org.processcene.core.ProcessCene;
import org.processcene.core.ResponseDocument;
import org.processcene.core.SearchEngineAdapter;
import org.processcene.core.SearchRequest;
import org.processcene.core.SearchResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Projections.meta;
import static com.mongodb.client.model.Projections.metaSearchScore;

public class AtlasAdapter implements SearchEngineAdapter {
  private final MongoDatabase database;
  private final MongoClient client;
  private final MongoCollection<Document> collection;
  private final List<SearchRequest> queries = new ArrayList<>();
  private final SearchOperator subset_filter;
  private final List<Document> raw_documents;

  public static List<Bson> doc_selector() {
    Bson match = Aggregates.match(new Document("cast", "Keanu Reeves"));
    Bson sort = Aggregates.sort(new Document("imdb.rating", -1));
    return Arrays.asList(match,sort);
  }

  public AtlasAdapter(String db, String coll) {
    String uri = System.getenv("ATLAS_URI");
    this.client = MongoClients.create(uri);
    this.database = this.client.getDatabase(db);
    this.collection = this.database.getCollection(coll);

    System.out.println("database: " + database.getName());
    System.out.println("collection = " + collection.getNamespace().getCollectionName());
    System.out.println("collection.countDocuments() = " + collection.countDocuments());

    List<Bson> doc_selector = doc_selector();

    List<Document> docs = new ArrayList<>();
    try {
      docs = collection.aggregate(doc_selector).into(new ArrayList<>());
      System.out.println(collection.aggregate(doc_selector).explain());  // TODO: capture and report?
    } catch (Exception e) {
      // TODO: handle better?
      System.err.println(e.getMessage());
    }

    System.out.println("doc_selector = " + doc_selector);
    System.out.println("docs.size() = " + docs.size());

    // The first `ProcessCene.MAX_DOCS` are all we keep
    raw_documents = docs.subList(0, (docs.size() > ProcessCene.MAX_DOCS) ? ProcessCene.MAX_DOCS : docs.size());

    // Build a selector for this subset of documents, so all searches are filtered by it
    List<ObjectId> obj_ids = new ArrayList<>();
    for (Document doc : raw_documents) {
      obj_ids.add(doc.getObjectId("_id"));
    }

    //      "in": {
    //        "path": "_id",
    //            "value": [ObjectId("5ca4bbcea2dd94ee58162a72"), ObjectId("5ca4bbcea2dd94ee58162a91")]
    //      }
    subset_filter = SearchOperator.of(new Document("in", new Document("path", "_id").append("value", obj_ids)));
    System.out.println("subset_filter = " + subset_filter);

    queries.add(new AtlasSearchRequest(
        SearchOperator.of(
            new Document("queryString",
                new Document("defaultPath", "title").append("query", "genres:Drama^2.0 OR genres:Romance^5.0")))
    ));

    queries.add(new AtlasSearchRequest(
        SearchOperator.of(
            new Document("queryString",
                new Document("defaultPath", "title.en").append("query", "river")))
    ));

    queries.add(new AtlasSearchRequest(
        SearchOperator.of(
            new Document("queryString",
                new Document("defaultPath", "title").append("query", "bill OR prince^5.0")))
    ));

    queries.add(new AtlasSearchRequest(
        SearchOperator.of(
            new Document("queryString",
                new Document("defaultPath", "title").append("query", "(open paren no close")))
    ));

    queries.add(new AtlasSearchRequest(
        SearchOperator.compound()
            .should(Arrays.asList(
                SearchOperator.text(SearchPath.fieldPath("genres"), "Romance"),
                SearchOperator.text(SearchPath.fieldPath("genres"), "Drama")
            ))
    ));
  }

  @Override
  public SearchResponse search(SearchRequest request) {
    AtlasSearchRequest asr = (AtlasSearchRequest) request;

    Bson search_stage = Aggregates.search(
        SearchOperator.compound()
            .filter(Arrays.asList(subset_filter))
            .must(Arrays.asList(asr.search_operator)),
        SearchOptions.searchOptions().option("scoreDetails", BsonBoolean.TRUE)
    );

    Bson project_stage =  project(fields(  // Include _id
        include("title", "cast", "genres"),
        metaSearchScore("score"),
        meta("scoreDetails", "searchScoreDetails")
    ));

    List<Bson> atlas_docs = new ArrayList<>();
    SearchResponse response = new SearchResponse();
    try {
      atlas_docs = collection.aggregate(Arrays.asList(search_stage,project_stage)).into(new ArrayList<>());
    } catch (Exception e) {
      response.error = e.getMessage();
    }

    for (Bson doc : atlas_docs) {
      BsonDocument atlas_doc = doc.toBsonDocument();

      ResponseDocument rd = new ResponseDocument();
      ObjectId obj_id = atlas_doc.getObjectId("_id").getValue();
      rd.fields.put("_id", obj_id);
      rd.fields.put("title", atlas_doc.getString("title").getValue());
      float score = (float) atlas_doc.getDouble("score").getValue();
      if (score > response.max_score) response.max_score = score;
      rd.score = score;

      for (int i = 0; i < raw_documents.size(); i++) {
        Document raw_document = raw_documents.get(i);
        if (raw_document.get("_id").equals(obj_id)) {
          rd.id = (i+1);  // 1-based
        }
      }

      response.documents.add(rd);
    }


    return response;
  }

  @Override
  public List<SearchRequest> getQueries() {
    return queries;
  }

  @Override
  public List<DocumentAvatar> getDocuments() {
    List<DocumentAvatar> docs = new ArrayList<>();
    for (int i = 0; i < raw_documents.size(); i++) {
      Document raw_document = raw_documents.get(i);
      Map<String, Object> d = new HashMap<>();

      d.put("id", (i+1));
      d.put("title", raw_document.getString("title"));
      d.put("type", "movie");

      docs.add(new DocumentAvatar(d, 0, 0));
    }

    return docs;
  }
}
