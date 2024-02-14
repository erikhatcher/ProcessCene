package com.mongodb.atlas.cene;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class S1E2 {
  public static void main(String[] args) {
    String uri = System.getenv("ATLAS_URI");
    MongoClient client = MongoClients.create(uri);
    MongoDatabase database = client.getDatabase("sample_mflix");
    MongoCollection<Document> collection = database.getCollection("movies");

    ArrayList<Document> docs = collection.aggregate(getAggregationPipeline()).into(new ArrayList<>());

    for (Document doc : docs) {
      System.out.println(doc.getString("title"));
    }
  }

  private static List<? extends Bson> getAggregationPipeline() {
    return Arrays.asList(new Document("$search",
        new Document("index", "default")
            .append("text",
                new Document("query", "keanu")
                    .append("path", "cast"))));
  }
}
