import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.store.ByteBuffersDirectory;
import processing.core.PApplet;
import processing.data.StringDict;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LuceneIndexingSlide extends BaseSlide {
  ByteBuffersDirectory index;

  List<Document> docs = new ArrayList<Document>();

  public LuceneIndexingSlide(String title, ProcessCene presentation) {
    super(title, presentation);

    Document doc1 = new Document();
    doc1.add(new StringField("id", "1", Field.Store.YES));
    doc1.add(new TextField("title", "The quick brown fox jumped over the lazy dogs.", Field.Store.YES));
    docs.add(doc1);

    Document doc2 = new Document();
    doc2.add(new StringField("id", "2", Field.Store.YES));
    doc2.add(new TextField("title", "The lazy dogs... slept.", Field.Store.YES));
    docs.add(doc2);

    index = new ByteBuffersDirectory();
    try {
      IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(new StandardAnalyzer()));

      for (Document doc : docs) {
        writer.addDocument(doc);
      }
      writer.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void draw(int step) {
    presentation.background(255,255,255);
    presentation.fill(0,0,0);
    presentation.textSize(14);

    String field_name = "title";
    float x = 20;
    float y = presentation.textAscent() + presentation.textDescent() + 10;

    presentation.text("Field: " + field_name, x,7);
    y += 20;

    for (Document doc : docs) {
      String doc_string = doc.get("id") + ": " + doc.get(field_name);
      presentation.text(doc_string,x,y);
      y += 40;
    }

    try {
      IndexReader reader = DirectoryReader.open(index);
      Terms terms = MultiTerms.getTerms(reader,"title");
      if (terms != null) {
        TermsEnum terms_enum = terms.iterator();
        while (terms_enum.next() != null) {
          int df = terms_enum.docFreq();
          String txt = terms_enum.term().utf8ToString();
          presentation.text("Term: " + txt + " (" + df + ")", x, y);

          y += presentation.textAscent() + presentation.textDescent() + 10;
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    super.draw(step);
  }
}
