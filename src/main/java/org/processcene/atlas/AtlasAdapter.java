package org.processcene.atlas;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.search.SearchOperator;
import com.mongodb.client.model.search.SearchOptions;
import org.bson.BsonArray;
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

import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Projections.meta;
import static com.mongodb.client.model.Projections.metaSearchScore;

public class AtlasAdapter implements SearchEngineAdapter {
  public final MongoDatabase database;
  private final MongoClient client;
  public final MongoCollection<Document> collection;
  private final Map<ObjectId, Integer> doc_map = new HashMap<>();
  public SearchOperator subset_filter = null;
  private List<Document> documents = new ArrayList<>();

  public AtlasAdapter(String db, String coll, List<Bson> doc_selector) {
    String uri = System.getenv("ATLAS_URI");
    this.client = MongoClients.create(uri);
    this.database = this.client.getDatabase(db);
    this.collection = this.database.getCollection(coll);

    List<Bson> ds = doc_selector;
    if (ds == null) {
      ds = Arrays.asList(new Document("$match", new Document()));
    }

    List<Document> docs = new ArrayList<>();
    try {
      long start = System.currentTimeMillis();
      docs = collection.aggregate(ds).into(new ArrayList<>());
      long end = System.currentTimeMillis();
      System.out.println("AtlasAdapter aggregate.into array: " + (end - start) + "ms");

      //System.out.println(collection.aggregate(ds).explain());  // TODO: capture and report?
    } catch (Exception e) {
      // TODO: handle better?
      System.err.println(e.getMessage());

      throw e;
    }

    // The first `ProcessCene.MAX_DOCS` are all we keep, unless we're after ALL of them
    documents = (doc_selector == null) ?
        docs :
        docs.subList(0, (docs.size() > ProcessCene.MAX_DOCS) ? ProcessCene.MAX_DOCS : docs.size());

    long start = System.currentTimeMillis();
    List<ObjectId> obj_ids = new ArrayList<>();
    for (int i = 0; i < documents.size(); i++) {
      Document doc = documents.get(i);
      int index = i + 1; // cross reference for each doc to know where in the list it is
      doc.put("index", index);

      ObjectId obj_id = documents.get(i).getObjectId("_id");
      obj_ids.add(obj_id);
      doc_map.put(obj_id, index); // cross-ref to know where an _id is in the full list
    }
    long end = System.currentTimeMillis();
    System.out.println("AtlasAdapter: building obj_id cross-ref: " + (end - start) + "ms");

    // Build a selector for this subset of documents, so all searches are filtered by it
    if (doc_selector != null) {
      //      "in": {
      //        "path": "_id",
      //            "value": [ObjectId("5ca4bbcea2dd94ee58162a72"), ObjectId("5ca4bbcea2dd94ee58162a91")]
      //      }
      subset_filter = SearchOperator.of(new Document("in", new Document("path", "_id").append("value", obj_ids)));
//    System.out.println("subset_filter = " + subset_filter);
    }
  }

  public AtlasAdapter(String database, String collection) {
    this(database, collection, null);
  }

  @Override
  public SearchResponse search(SearchRequest request) {
    AtlasSearchRequest asr = (AtlasSearchRequest) request;

    Bson search_stage;
//      "tracking": {
//        "searchTerms": "<term-to-search>"
//      }
    SearchOptions opts = SearchOptions.searchOptions()
        .option("scoreDetails", BsonBoolean.TRUE)
        .option("tracking", new Document("searchTerms", asr.query_string));

    if (subset_filter != null) {
      search_stage = Aggregates.search(
          SearchOperator.compound()
              .filter(Arrays.asList(subset_filter))
              .must(Arrays.asList(asr.search_operator)),
          opts
      );
    } else {
      search_stage = Aggregates.search(asr.search_operator, opts);

      System.out.println("AtlasAdapter search_stage: " + search_stage.toBsonDocument().toJson());
    }

    // TODO: Note/doc this - the aggregation call to bring, say, 14k docs into a List takes many seconds
    Bson limit_stage = limit(10);

    Bson project_stage = project(fields(  // Include _id
        include("title", "cast", "genres"),
        metaSearchScore("score"),
        meta("scoreDetails", "searchScoreDetails")
    ));

    List<Bson> search_results = new ArrayList<>();
    SearchResponse response = new SearchResponse();
    try {


      long start = System.currentTimeMillis();
      AggregateIterable<Document> aggregate = collection.aggregate(Arrays.asList(search_stage, project_stage, limit_stage));
      for (Document document : aggregate) {
        search_results.add(document);
      }
      long end = System.currentTimeMillis();
      long query_time = end - start;
      response.query_time = query_time;
      System.out.println("AtlasAdapter: search aggregate: " + query_time + "ms");

      Document explain = collection.aggregate(Arrays.asList(search_stage, project_stage)).explain();
      List<Document> explain_stages = (List<Document>) explain.get("stages");
      response.explain = ((Document) explain_stages.get(0).get("$_internalSearchMongotRemote")).get("explain").toString();
    } catch (Exception e) {
      response.error = e.getMessage();
    }

    for (int i = 0; i < search_results.size(); i++) {
      Bson hit = search_results.get(i);
      BsonDocument atlas_doc = hit.toBsonDocument();
      ObjectId obj_id = atlas_doc.getObjectId("_id").getValue();

      ResponseDocument rd = new ResponseDocument();

      int index = doc_map.get(obj_id);
      Document document = documents.get(index - 1);
      rd.id = index;  //

      rd.fields.put("_id", obj_id);
      rd.fields.put("title", document.getString("title"));
      float score = (float) atlas_doc.getDouble("score").getValue();
      if (score > response.max_score) response.max_score = score;
      rd.score = score;
      rd.explain = prettyScoreDetails(0, atlas_doc.getDocument("scoreDetails"));
      rd.fields.put("score_details", atlas_doc.get("scoreDetails"));

      response.documents.add(rd);
    }

//    response.print();

    response.parsed_query = "TBD: pull what explain() provides";
    return response;
  }

  private String prettyScoreDetails(int indentLevel, BsonDocument scoreDetails) {
    String spaces = " ".repeat(indentLevel);

    StringBuilder output = new StringBuilder();

    output.append(spaces + scoreDetails.getDouble("value").doubleValue() + ", " +
        scoreDetails.getString("description").getValue() + "\n");
    BsonArray details = scoreDetails.getArray("details");
    for (org.bson.BsonValue detail : details) {
      output.append(prettyScoreDetails(indentLevel + 2, (BsonDocument) detail));
    }

    return output.toString();
  }


//  @Override
//  public List<SearchRequest> getQueries() {
//    return queries;
//  }

  @Override
  public List<DocumentAvatar> getDocuments() {
    List<DocumentAvatar> docs = new ArrayList<>();

    int index = 1;
    for (Document raw_document : documents) {
      //      System.out.println("raw_document = " + raw_document.toJson());

      Map<String, Object> d = new HashMap<>();

      d.put("id", index);
      d.put("title", raw_document.getString("title"));
      d.put("type", "movie");

      for (String f : raw_document.keySet()) {
        d.put(f, raw_document.get(f));
      }


      docs.add(new DocumentAvatar(d));

      index++;
    }

    return docs;
  }
}
