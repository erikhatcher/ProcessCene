package org.processcene.atlas;

import com.mongodb.client.model.search.SearchOperator;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.processcene.core.ConsoleOutputSlideBase;
import org.processcene.core.DocumentAvatar;
import org.processcene.core.ProcessCene;
import org.processcene.core.SearchResponse;
import org.processcene.core.Tagger;
import org.processcene.solr.SolrTagger;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.search.SearchPath.fieldPath;

public class AtlasSearchOnSolrTagger extends ConsoleOutputSlideBase {
  List<String> descriptions = new ArrayList<>();
  List<String> outputs = new ArrayList<>();
  private List<DocumentAvatar> documents;

  public AtlasSearchOnSolrTagger(String title) {
    super(title);
  }

  @Override
  public void init(ProcessCene p) {
    super.init(p);

    AtlasAdapter atlas = new AtlasAdapter("sample_mflix","movies");

    String utterance = "what drama movies star keanu reeves";

    Tagger tagger = new SolrTagger();  // TODO: replace with $searchMeta: { "tagger": ... }
    StringBuilder output = new StringBuilder();
    output.append("Tagging: " + utterance + "\n");
    JSONObject tags = tagger.tag(utterance);

    // TODO: pull tag handling JSON specifics out of here

    output.append("\ntags = " + tags.toJSONString()  + "\n");

    // {"tagsCount":1,"response":{"docs":[{"_version_":1772684818371313709,"name":["Keanu Reeves"],"id":"cast-Keanu Reeves","type":"cast"}]

    String error_message = (String) tags.get("error");
    if (error_message != null) {
      output.append("\nTagging ERROR: " + error_message  + "\n");
    } else {
      JSONArray tagged_documents = (JSONArray) ((JSONObject) tags.get("response")).get("docs");

      // Ignore position offsets of tagged text, simply apply the tagged entities that we know about to
      // an Atlas $search
      
      List<SearchOperator> tagged_clauses = new ArrayList<>();

      for (int i = 0; i < tagged_documents.size(); i++) {
        JSONObject tagged_doc = (JSONObject) tagged_documents.get(i);
        String id = (String) tagged_doc.get("id");
        String type = (String) tagged_doc.get("type");
        // extract the value to query Atlas Search with from the first element of our names[] array // TODO: refactor to use a value single valued string field
        String value = (String) ((JSONArray) tagged_doc.get("name")).get(0); // TODO: brittle - save actual value as `value_s` on the tagger collection

        if ("cast".equals(type)) {
          output.append("CAST: " + value + "\n");

          Document pq = new Document("phrase",
              new Document("query", value)
                  .append("path", fieldPath("cast")));
          tagged_clauses.add(SearchOperator.of(pq));
        }

        if ("genre".equals(type)) {
          output.append("GENRE: " + value + "\n");

          Document pq = new Document("phrase",
              new Document("query", value)
                  .append("path", fieldPath("genres")));
          tagged_clauses.add(SearchOperator.of(pq));
        }
        
        // TODO: remove the tagged text (using the offsets) from the query, and _should_ it in below
      }

      SearchResponse response = atlas.search(new AtlasSearchRequest(SearchOperator.compound().must(tagged_clauses)));

      // Print out each returned result
      //aggregation_spec.forEach(doc -> System.out.println(formatJSON(doc)));
      response.documents.forEach(d -> {
        output.append(d.fields.get("title") + "\n");
//        output.append("  Cast: " + ((BsonArray)doc.get("cast")).getValues() + "\n");
//        output.append("  Genres: " + ((BsonArray)doc.get("genres")).getValues() + "\n");
        output.append("  Score:" + d.score + "\n");
//        printScoreDetails(2, doc.toBsonDocument().getDocument("scoreDetails"));
        output.append("\n");
      });
    }

    descriptions.add(utterance);
    outputs.add(output.toString());
  }

  @Override
  protected String getDescription(int step) {
    return descriptions.get(step);
  }

  @Override
  protected String getOutput(int step) {
    return outputs.get(step);
  }
}
