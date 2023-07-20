import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.KnnFloatVectorField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.FloatVectorValues;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.index.VectorSimilarityFunction;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.KnnFloatVectorQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import processing.core.PImage;
import processing.core.PVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.apache.lucene.search.DocIdSetIterator.NO_MORE_DOCS;

public class VectorSearchSlide extends BaseSlide {
  private static int MAX_PIXELS = 400;
  private final List<float[]> sample_vectors = new ArrayList<>();
  private final PImage[] doc_images = new PImage[10];  // icons for numbers 0 - 9
  private final PImage[] doc_images_inverted = new PImage[10];  // icons for numbers 0 - 9
  private final PImage search_icon;
  private final PImage lucene_logo;

  public VectorSearchSlide(String title, ProcessCene presentation) {
    super(title, presentation);

    sample_vectors.add(new float[]{0.50f, 0.50f});     // 1
    sample_vectors.add(new float[]{0.27f, 0.32f});     // 2
    sample_vectors.add(new float[]{0.13f, -0.69f});    // 3
    sample_vectors.add(new float[]{0.42f, 0.72f});     // 4
    sample_vectors.add(new float[]{-0.25f, -0.65f});   // 5
    sample_vectors.add(new float[]{0.19f, 0.32f});     // 6
    sample_vectors.add(new float[]{-0.63f, -0.92f});   // 7
    sample_vectors.add(new float[]{0.53f, 0.12f});     // 8
    sample_vectors.add(new float[]{-0.23f, 0.74f});    // 9

    for (int i=0; i < 10; i++) {
      doc_images[i] = presentation.loadImage(presentation.getFilePathFromResources("Assets/normal/" + i + "_10x.png"));
      doc_images[i].resize(30,30);
      doc_images_inverted[i] = presentation.loadImage(presentation.getFilePathFromResources("Assets/normal/" + i + "_Inverted10x.png"));
      doc_images_inverted[i].resize(30,30);
    }

    lucene_logo = presentation.loadImage(presentation.getFilePathFromResources("lucene_green_300.png"));
    lucene_logo.resize(300,0);

    search_icon = presentation.loadImage(presentation.getFilePathFromResources("Assets/normal/Technical_ATLAS_Search10x.png"));
    search_icon.resize(30,30);
  }

  @Override
  public void draw(int step) {
    if (lucene_logo != null) {
      presentation.image(lucene_logo, 10, 10);
    }


    try {
      ByteBuffersDirectory index = new ByteBuffersDirectory();
      IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(new SimpleAnalyzer()));

      for (int i=0; i < sample_vectors.size(); i++) {
        Document doc = new Document();
        doc.add(new StringField("id","" + (i + 1), Field.Store.YES));
        doc.add(new KnnFloatVectorField("vector", sample_vectors.get(i),
            (getCurrentVariation() == 1) ? VectorSimilarityFunction.EUCLIDEAN : VectorSimilarityFunction.COSINE));
        writer.addDocument(doc);
      }

      writer.close();

      presentation.translate(presentation.width / 2,presentation.height / 2);
      presentation.line(-MAX_PIXELS,0,MAX_PIXELS,0);
      presentation.line(0,-MAX_PIXELS,0,MAX_PIXELS);
      presentation.text((getCurrentVariation() == 1) ? "Euclidean" : "Cosine", -MAX_PIXELS, -MAX_PIXELS + presentation.textAscent());

      DirectoryReader r = DirectoryReader.open(index);

      int[] lucene_docid_to_sample_index = new int[sample_vectors.size()];
      for (LeafReaderContext ctx : r.leaves()) {
        FloatVectorValues vectorValues = ctx.reader().getFloatVectorValues("vector");
        if (vectorValues != null) {
          StoredFields storedFields = ctx.reader().storedFields();
          while (vectorValues.nextDoc() != NO_MORE_DOCS) {
            float[] v = vectorValues.vectorValue();
            Document d = storedFields.document(vectorValues.docID(), Set.of("id"));
            int id = Integer.parseInt(d.get("id"));

            // TODO: this is a hack until I figure out how to retrieve stored vectors by docid
            lucene_docid_to_sample_index[vectorValues.docID()] = id - 1;

            PVector pv = vector_for(v[0],v[1]);
            presentation.line(0,0,pv.x,pv.y);
            presentation.circle(pv.x, pv.y, 2);
            presentation.image(doc_images[id], pv.x, pv.y - presentation.textAscent() - presentation.textDescent());
          }
        }
      }

      if (step > 0) {
        IndexSearcher searcher = new IndexSearcher(r);
        float[] v = new float[]{0.25f, 0.15f};
        PVector query_vector = vector_for(v[0], v[1]);
        presentation.stroke(presentation.spring_green);
        presentation.line(0,0,query_vector.x,query_vector.y);
        presentation.circle(query_vector.x, query_vector.y, 2);
        presentation.image(search_icon, query_vector.x, query_vector.y - presentation.textAscent() - presentation.textDescent());

        if (step > 1) {
          TopDocs nearest_docs = searcher.search(new KnnFloatVectorQuery("vector", v, 2), 10);
          for (ScoreDoc scoreDoc : nearest_docs.scoreDocs) {
            int sample_index = lucene_docid_to_sample_index[scoreDoc.doc];
            v = sample_vectors.get(sample_index);
            PVector result_vector = vector_for(v[0], v[1]);
            presentation.image(doc_images_inverted[sample_index + 1], result_vector.x, result_vector.y - presentation.textAscent() - presentation.textDescent());

            if (step > 2) {
              presentation.stroke(presentation.spring_green);
              if (getCurrentVariation() == 1) {
                // Euclidean
                presentation.line(result_vector.x, result_vector.y, query_vector.x, query_vector.y);
              } else {
                presentation.noFill();
                float h1 = query_vector.heading();
                float h2 = result_vector.heading();
                if (h1 < h2) {
                  presentation.arc(0, 0, 100, 100, h1, h2);
                } else {
                  presentation.arc(0, 0, 200, 200, h2, h1);
                }
              }
            }
          }
        }
      }


    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    super.draw(step);
  }

  private float map(float value) {
    return -MAX_PIXELS + ((MAX_PIXELS - -MAX_PIXELS) * (value - -1.0f)) / (1.0f - -1.0f);
  }

  @Override
  public int getNumberOfVariations() {
    return 2;
  }

  @Override
  public int getNumberOfSteps() {
    return 3;
  }

  private PVector vector_for(float x, float y) {
    // we invert the y coordinate, until we figure out how to do 8th grade math coordinate system
    return new PVector(map(x), -map(y));
  }
}
