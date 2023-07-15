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
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.ByteBuffersDirectory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LuceneIndexingSlide extends BaseSlide {
  ByteBuffersDirectory index;

  List<Document> docs = new ArrayList<Document>();

  public LuceneIndexingSlide(String title, ProcessCene presentation) {
    super(title, presentation);

    // TODO: Add images for documents and such
    // Assets/normal/Technical_MDB_DocumentModel10x.png
    // Technical_MDB_SearchCollection_10x.png
    // General_ACTION_Favorite_Inverted10x.png
    // Technical_MDB_DocumentModel10x.png

    Document doc1 = new Document();
    doc1.add(new StringField("id", "0", Field.Store.YES));
    doc1.add(new TextField("title", "Stella and Erik sitting in a tree!", Field.Store.YES));
    docs.add(doc1);

    Document doc2 = new Document();
    doc2.add(new StringField("id", "1", Field.Store.YES));
    doc2.add(new TextField("title", "Erik loves Stella!!!!!", Field.Store.YES));
    docs.add(doc2);
  }

  @Override
  public void draw(int step) {
    index = new ByteBuffersDirectory();
    try {
      IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(new StandardAnalyzer()));

      for (int i=0; i < step; i++) {
        writer.addDocument(docs.get(i));
      }
      writer.close();
    } catch (Exception e) {
      System.err.println(e.getLocalizedMessage());
    }

    presentation.background(255,255,255);
    presentation.fill(0,0,0);
    presentation.textSize(14);

    String field_name = "title";
    float x = 20;
    float y = presentation.textAscent() + presentation.textDescent() + 10;

    presentation.text("Field: " + field_name, x,y);
    y += 20;

    for (int i = 0; i < step; i++) {
      Document doc = docs.get(i);
      String doc_string = doc.get("id") + ": " + doc.get(field_name);
      presentation.text(doc_string,x,y);
      y += 40;
    }

    x = 600;
    y = 20;
    try {
      IndexReader reader = DirectoryReader.open(index);
      Terms terms = MultiTerms.getTerms(reader,"title");
      if (terms != null) {
        TermsEnum terms_enum = terms.iterator();
        while (terms_enum.next() != null) {
          int df = terms_enum.docFreq();
          String txt = terms_enum.term().utf8ToString();

          String postings_list = "Postings: ";
          PostingsEnum postings = terms_enum.postings(null);
          while (postings.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
            postings_list += postings.docID() + " - ";
          }
          presentation.text("Term: " + txt + " (" + df + ") :: " + postings_list, x, y);

          y += presentation.textAscent() + presentation.textDescent() + 10;
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    super.draw(step);
  }

  @Override
  public int getNumberOfSteps() {
    return docs.size();
  }
}
